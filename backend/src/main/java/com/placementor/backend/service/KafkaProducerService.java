package com.placementor.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.placementor.backend.dto.InterviewFeedbackEvent;
import com.placementor.backend.dto.InterviewQuestionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducerService.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topic.interview-questions:interview-questions-topic}")
    private String interviewQuestionsTopic;

    @Value("${kafka.topic.interview-feedback:interview-feedback-topic}")
    private String interviewFeedbackTopic;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendInterviewQuestionEvent(InterviewQuestionEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            log.info("Sending InterviewQuestionEvent to Kafka topic {}: {}", interviewQuestionsTopic, message);
            kafkaTemplate.send(interviewQuestionsTopic, String.valueOf(event.getInterviewId()), message);
        } catch (Exception e) {
            log.error("Failed to send InterviewQuestionEvent to Kafka", e);
            throw new RuntimeException("Kafka publish error", e);
        }
    }

    public void sendInterviewFeedbackEvent(InterviewFeedbackEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            log.info("Sending InterviewFeedbackEvent to Kafka topic {}: {}", interviewFeedbackTopic, message);
            kafkaTemplate.send(interviewFeedbackTopic, String.valueOf(event.getInterviewId()), message);
        } catch (Exception e) {
            log.error("Failed to send InterviewFeedbackEvent to Kafka", e);
            throw new RuntimeException("Kafka publish error", e);
        }
    }
}
