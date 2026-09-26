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

        Resume resume = resumeRepository.findTopByUserIdOrderByIdDesc(userId).orElse(null);
        String resumeText = (resume != null && resume.getExtractedText() != null && !resume.getExtractedText().isBlank())
                ? resume.getExtractedText()
                : "Target Role: " + role + ". No resume uploaded yet.";

        try {
            String rawQuestions = geminiService.generateInterviewQuestions(resumeText, role);
            List<Map<String, String>> parsed = parseQuestionsFromText(rawQuestions);
            if (parsed.isEmpty()) {
                parsed = parseQuestionsFromText(geminiService.generateInterviewQuestions(null, role));
            }
            String questionsJson = objectMapper.writeValueAsString(parsed);

            Interview interview = Interview.builder()
                    .userId(userId)
                    .role(role)
                    .questions(questionsJson)
                    .status("COMPLETED")
                    .build();

            interview = interviewRepository.save(interview);

            if (kafkaEnabled && kafkaProducerService != null) {
                try {
                    InterviewQuestionEvent event = InterviewQuestionEvent.builder()
                            .interviewId(interview.getId())
                            .resumeText(resumeText)
                            .role(role)
                            .build();
                    kafkaProducerService.sendInterviewQuestionEvent(event);
                } catch (Exception e) {
                    log.warn("Kafka event publish log: {}", e.getMessage());
                }
            }

            List<Map<String, Object>> questionsList = parseQuestionsJson(questionsJson);

            return InterviewResponseDto.builder()
                    .id(interview.getId())
                    .role(interview.getRole())
                    .questions(questionsList)
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

        // Self-heal: if interview status is stuck in PROCESSING or questions array is empty, generate questions now
        if ("PROCESSING".equals(interview.getStatus()) || interview.getQuestions() == null || interview.getQuestions().equals("[]") || interview.getQuestions().isBlank()) {
            try {
                Resume resume = resumeRepository.findTopByUserIdOrderByIdDesc(userId).orElse(null);
                String resumeText = (resume != null && resume.getExtractedText() != null && !resume.getExtractedText().isBlank())
                        ? resume.getExtractedText()
                        : "Target Role: " + interview.getRole() + ". No resume uploaded yet.";
                String rawQuestions = geminiService.generateInterviewQuestions(resumeText, interview.getRole());
                List<Map<String, String>> parsed = parseQuestionsFromText(rawQuestions);
                if (parsed.isEmpty()) {
                    parsed = parseQuestionsFromText(geminiService.generateInterviewQuestions(null, interview.getRole()));
                }
                String questionsJson = objectMapper.writeValueAsString(parsed);
                interview.setQuestions(questionsJson);
                interview.setStatus("COMPLETED");
                interviewRepository.save(interview);
            } catch (Exception e) {
                log.error("Self-healing question generation failed for interview id {}", interviewId, e);
            }
        }

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
            String feedbackJsonStr = geminiService.generateFeedback(interview.getQuestions(), answerTextBuilder.toString());
            int overallScore = 85;
            try {
                JsonNode feedbackNode = objectMapper.readTree(feedbackJsonStr);
                overallScore = feedbackNode.path("overall_score").asInt(85);
            } catch (Exception ignored) {}

            interview.setAnswers(answersJson);
            interview.setFeedback(feedbackJsonStr);
            interview.setScore(overallScore);
            interview.setStatus("COMPLETED");
            interviewRepository.save(interview);

            if (kafkaEnabled && kafkaProducerService != null) {
                try {
                    InterviewFeedbackEvent event = InterviewFeedbackEvent.builder()
                            .interviewId(interview.getId())
                            .questionsJson(interview.getQuestions())
                            .answerText(answerTextBuilder.toString())
                            .answersJson(answersJson)
                            .build();
                    kafkaProducerService.sendInterviewFeedbackEvent(event);
                } catch (Exception e) {
                    log.warn("Kafka event publish log: {}", e.getMessage());
                }
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
