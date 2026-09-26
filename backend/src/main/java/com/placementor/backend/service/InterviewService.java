package com.placementor.backend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.placementor.backend.dto.*;
import com.placementor.backend.entity.Interview;
import com.placementor.backend.entity.Resume;
import com.placementor.backend.repository.InterviewRepository;
import com.placementor.backend.repository.ResumeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class InterviewService {

    private static final Logger log = LoggerFactory.getLogger(InterviewService.class);

    private final InterviewRepository interviewRepository;
    private final ResumeRepository resumeRepository;
    private final KafkaProducerService kafkaProducerService;
    private final GeminiService geminiService;
    private final ObjectMapper objectMapper;

    @Value("${kafka.enabled:true}")
    private boolean kafkaEnabled;

    public InterviewService(InterviewRepository interviewRepository,
                            ResumeRepository resumeRepository,
                            @Autowired(required = false) KafkaProducerService kafkaProducerService,
                            GeminiService geminiService,
                            ObjectMapper objectMapper) {
        this.interviewRepository = interviewRepository;
        this.resumeRepository = resumeRepository;
        this.kafkaProducerService = kafkaProducerService;
        this.geminiService = geminiService;
        this.objectMapper = objectMapper;
    }

    public InterviewResponseDto generateInterview(Long userId, String role) {
        if (role == null || role.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role is required");
        }

        Resume resume = resumeRepository.findTopByUserIdOrderByIdDesc(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resume not found"));

        try {
            Interview interview = Interview.builder()
                    .userId(userId)
                    .role(role)
                    .questions("[]")
                    .status("PROCESSING")
                    .build();

            interview = interviewRepository.save(interview);

            if (kafkaEnabled && kafkaProducerService != null) {
                // Publish Kafka event for asynchronous question generation
                InterviewQuestionEvent event = InterviewQuestionEvent.builder()
                        .interviewId(interview.getId())
                        .resumeText(resume.getExtractedText())
                        .role(role)
                        .build();

                kafkaProducerService.sendInterviewQuestionEvent(event);
            } else {
                // Async fallback if Kafka is not active
                final Long finalInterviewId = interview.getId();
                final String resumeText = resume.getExtractedText();
                CompletableFuture.runAsync(() -> {
                    try {
                        String rawQuestions = geminiService.generateInterviewQuestions(resumeText, role);
                        List<Map<String, String>> parsed = parseQuestionsFromText(rawQuestions);
                        String questionsJson = objectMapper.writeValueAsString(parsed);
                        Interview inv = interviewRepository.findById(finalInterviewId).orElse(null);
                        if (inv != null) {
                            inv.setQuestions(questionsJson);
                            inv.setStatus("COMPLETED");
                            interviewRepository.save(inv);
                        }
                    } catch (Exception e) {
                        log.error("Async background generation failed for interview id {}", finalInterviewId, e);
                        Interview inv = interviewRepository.findById(finalInterviewId).orElse(null);
                        if (inv != null) {
                            inv.setStatus("FAILED");
                            interviewRepository.save(inv);
                        }
                    }
                });
            }

            return InterviewResponseDto.builder()
                    .id(interview.getId())
                    .role(interview.getRole())
                    .questions(Collections.emptyList())
                    .status(interview.getStatus())
                    .submitted(false)
                    .createdAt(String.valueOf(interview.getId()))
                    .build();
        } catch (Exception e) {
            log.error("Failed to initiate interview question generation", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create interview session");
        }
    }

    public List<InterviewResponseDto> getInterviews(Long userId) {
        List<Interview> interviews = interviewRepository.findByUserId(userId);
        List<InterviewResponseDto> result = new ArrayList<>();

        for (Interview interview : interviews) {
            List<Map<String, Object>> questionsList = parseQuestionsJson(interview.getQuestions());
            result.add(InterviewResponseDto.builder()
                    .id(interview.getId())
                    .role(interview.getRole())
                    .questions(questionsList)
                    .status(interview.getStatus())
                    .submitted(interview.getAnswers() != null)
                    .createdAt(String.valueOf(interview.getId()))
                    .build());
        }

        return result;
    }

    public InterviewResponseDto getInterview(Long interviewId, Long userId) {
        Interview interview = interviewRepository.findByIdAndUserId(interviewId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Interview not found"));

        List<Map<String, Object>> questionsList = parseQuestionsJson(interview.getQuestions());

        return InterviewResponseDto.builder()
                .id(interview.getId())
                .role(interview.getRole())
                .questions(questionsList)
                .status(interview.getStatus())
                .submitted(interview.getAnswers() != null)
                .createdAt(String.valueOf(interview.getId()))
                .build();
    }

    public Map<String, String> submitInterview(Long interviewId, Long userId, InterviewSubmitDto dto) {
        Interview interview = interviewRepository.findByIdAndUserId(interviewId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Interview not found"));

        try {
            List<InterviewSubmitDto.AnswerItem> answers = dto.getAnswers() != null ? dto.getAnswers() : Collections.emptyList();
            StringBuilder answerTextBuilder = new StringBuilder();
            for (InterviewSubmitDto.AnswerItem a : answers) {
                answerTextBuilder.append(a.getQuestionId()).append(": ").append(a.getAnswer()).append("\n");
            }

            String answersJson = objectMapper.writeValueAsString(answers);
            interview.setAnswers(answersJson);
            interview.setStatus("FEEDBACK_PROCESSING");
            interviewRepository.save(interview);

            if (kafkaEnabled && kafkaProducerService != null) {
                // Publish Kafka event for asynchronous feedback evaluation
                InterviewFeedbackEvent event = InterviewFeedbackEvent.builder()
                        .interviewId(interview.getId())
                        .questionsJson(interview.getQuestions())
                        .answerText(answerTextBuilder.toString())
                        .answersJson(answersJson)
                        .build();

                kafkaProducerService.sendInterviewFeedbackEvent(event);
            } else {
                // Async fallback if Kafka is not active
                final Long finalInterviewId = interview.getId();
                final String questionsJson = interview.getQuestions();
                final String answerText = answerTextBuilder.toString();
                final String finalAnswersJson = answersJson;
                CompletableFuture.runAsync(() -> {
                    try {
                        String feedbackJsonStr = geminiService.generateFeedback(questionsJson, answerText);
                        int overallScore = 0;
                        try {
                            JsonNode feedbackNode = objectMapper.readTree(feedbackJsonStr);
                            overallScore = feedbackNode.path("overall_score").asInt(0);
                        } catch (Exception ignored) {}

                        Interview inv = interviewRepository.findById(finalInterviewId).orElse(null);
                        if (inv != null) {
                            inv.setAnswers(finalAnswersJson);
                            inv.setFeedback(feedbackJsonStr);
                            inv.setScore(overallScore);
                            inv.setStatus("COMPLETED");
                            interviewRepository.save(inv);
                        }
                    } catch (Exception e) {
                        log.error("Async background feedback calculation failed for interview id {}", finalInterviewId, e);
                        Interview inv = interviewRepository.findById(finalInterviewId).orElse(null);
                        if (inv != null) {
                            inv.setStatus("FAILED");
                            interviewRepository.save(inv);
                        }
                    }
                });
            }

            Map<String, String> response = new HashMap<>();
            response.put("message", "Submitted successfully");
            return response;
        } catch (Exception e) {
            log.error("Error submitting interview answers", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to submit interview");
        }
    }

    public InterviewFeedbackDto getFeedback(Long interviewId, Long userId) {
        Interview interview = interviewRepository.findByIdAndUserId(interviewId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Interview not found"));

        List<Map<String, Object>> questions = parseQuestionsJson(interview.getQuestions());
        List<Map<String, Object>> answers = parseAnswersJson(interview.getAnswers());

        int overallScore = interview.getScore() != null ? interview.getScore() : 0;
        Map<String, Map<String, Object>> geminiItems = new HashMap<>();

        if (interview.getFeedback() != null && !interview.getFeedback().isBlank()) {
            try {
                JsonNode root = objectMapper.readTree(interview.getFeedback());
                if (root.has("overall_score")) {
                    overallScore = root.path("overall_score").asInt(overallScore);
                }
                JsonNode itemsNode = root.path("items");
                if (itemsNode.isArray()) {
                    for (JsonNode itemNode : itemsNode) {
                        String qId = itemNode.path("question_id").asText("");
                        int score = itemNode.path("score").asInt(0);
                        String fb = itemNode.path("feedback").asText("No feedback available.");
                        Map<String, Object> map = new HashMap<>();
                        map.put("score", score);
                        map.put("feedback", fb);
                        geminiItems.put(qId, map);
                    }
                }
            } catch (Exception e) {
                log.warn("Failed to parse feedback JSON string", e);
            }
        }

        List<FeedbackItemDto> items = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            Map<String, Object> q = questions.get(i);
            String qId = (String) q.getOrDefault("id", "q" + (i + 1));
            String qText = (String) q.getOrDefault("question", "");

            String userAns = "";
            if (i < answers.size()) {
                userAns = String.valueOf(answers.get(i).getOrDefault("answer", ""));
            }

            Map<String, Object> gf = geminiItems.getOrDefault(qId, Collections.emptyMap());
            int score = (int) gf.getOrDefault("score", 0);
            String feedbackStr = (String) gf.getOrDefault("feedback", "No feedback available.");

            items.add(FeedbackItemDto.builder()
                    .questionId(qId)
                    .question(qText)
                    .answer(userAns)
                    .score(score)
                    .feedback(feedbackStr)
                    .build());
        }

        return InterviewFeedbackDto.builder()
                .overallScore(overallScore)
                .status(interview.getStatus())
                .items(items)
                .build();
    }

    private List<Map<String, Object>> parseQuestionsJson(String json) {
        if (json == null || json.isBlank()) return Collections.emptyList();
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private List<Map<String, Object>> parseAnswersJson(String json) {
        if (json == null || json.isBlank()) return Collections.emptyList();
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private List<Map<String, String>> parseQuestionsFromText(String text) {
        List<Map<String, String>> questions = new ArrayList<>();
        if (text == null) return questions;
        String[] lines = text.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            boolean startsWithNumber = false;
            for (int i = 1; i <= 10; i++) {
                if (line.startsWith(i + ".")) {
                    startsWithNumber = true;
                    break;
                }
            }
            if (!startsWithNumber) continue;
            String[] parts = line.split("\\.", 2);
            if (parts.length > 1) {
                String questionContent = parts[1].trim();
                Map<String, String> qMap = new HashMap<>();
                qMap.put("id", "q" + (questions.size() + 1));
                qMap.put("question", questionContent);
                questions.add(qMap);
            }
        }
        return questions;
    }
}
