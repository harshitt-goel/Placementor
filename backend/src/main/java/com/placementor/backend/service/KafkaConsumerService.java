package com.placementor.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.placementor.backend.dto.InterviewFeedbackEvent;
import com.placementor.backend.dto.InterviewQuestionEvent;
import com.placementor.backend.entity.Interview;
import com.placementor.backend.repository.InterviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true", matchIfMissing = true)
public class KafkaConsumerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerService.class);

    private final GeminiService geminiService;
    private final InterviewRepository interviewRepository;
    private final ObjectMapper objectMapper;

    public KafkaConsumerService(GeminiService geminiService, InterviewRepository interviewRepository, ObjectMapper objectMapper) {
        this.geminiService = geminiService;
        this.interviewRepository = interviewRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${kafka.topic.interview-questions:interview-questions-topic}", groupId = "${spring.kafka.consumer.group-id:placementor-group}")
    public void consumeQuestionEvent(String message) {
        log.info("Received Kafka InterviewQuestionEvent: {}", message);
        try {
            InterviewQuestionEvent event = objectMapper.readValue(message, InterviewQuestionEvent.class);
            Long interviewId = event.getInterviewId();

            String rawQuestions = geminiService.generateInterviewQuestions(event.getResumeText(), event.getRole());
            List<Map<String, String>> parsedQuestions = parseQuestions(rawQuestions);
            String questionsJson = objectMapper.writeValueAsString(parsedQuestions);

            Interview interview = interviewRepository.findById(interviewId).orElse(null);
            if (interview != null) {
                interview.setQuestions(questionsJson);
                interview.setStatus("COMPLETED");
                interviewRepository.save(interview);
                log.info("Successfully generated and saved questions for interview id: {}", interviewId);
            }
        } catch (Exception e) {
            log.error("Error processing interview question event: {}", message, e);
            try {
                InterviewQuestionEvent event = objectMapper.readValue(message, InterviewQuestionEvent.class);
                Interview interview = interviewRepository.findById(event.getInterviewId()).orElse(null);
                if (interview != null) {
                    interview.setStatus("FAILED");
                    interviewRepository.save(interview);
                }
            } catch (Exception ex) {
                log.error("Failed to update interview status to FAILED", ex);
            }
        }
    }

    @KafkaListener(topics = "${kafka.topic.interview-feedback:interview-feedback-topic}", groupId = "${spring.kafka.consumer.group-id:placementor-group}")
    public void consumeFeedbackEvent(String message) {
        log.info("Received Kafka InterviewFeedbackEvent: {}", message);
        try {
            InterviewFeedbackEvent event = objectMapper.readValue(message, InterviewFeedbackEvent.class);
            Long interviewId = event.getInterviewId();

            String feedbackJsonStr = geminiService.generateFeedback(event.getQuestionsJson(), event.getAnswerText());

            Integer overallScore = 0;
            try {
                JsonNode feedbackNode = objectMapper.readTree(feedbackJsonStr);
                overallScore = feedbackNode.path("overall_score").asInt(0);
            } catch (Exception ignored) {}

            Interview interview = interviewRepository.findById(interviewId).orElse(null);
            if (interview != null) {
                interview.setAnswers(event.getAnswersJson());
                interview.setFeedback(feedbackJsonStr);
                interview.setScore(overallScore);
                interview.setStatus("COMPLETED");
                interviewRepository.save(interview);
                log.info("Successfully generated and saved feedback for interview id: {}", interviewId);
            }
        } catch (Exception e) {
            log.error("Error processing interview feedback event: {}", message, e);
            try {
                InterviewFeedbackEvent event = objectMapper.readValue(message, InterviewFeedbackEvent.class);
                Interview interview = interviewRepository.findById(event.getInterviewId()).orElse(null);
                if (interview != null) {
                    interview.setStatus("FAILED");
                    interviewRepository.save(interview);
                }
            } catch (Exception ex) {
                log.error("Failed to update interview status to FAILED", ex);
            }
        }
    }

    private List<Map<String, String>> parseQuestions(String text) {
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
