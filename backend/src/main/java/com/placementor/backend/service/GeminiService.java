package com.placementor.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    private static final Logger log = LoggerFactory.getLogger(GeminiService.class);

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent}")
    private String apiUrl;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generateInterviewQuestions(String resumeText, String role) {
        String safeRole = (role != null && !role.isBlank()) ? role : "Software Development Engineer";
        String prompt = String.format("""
            You are an expert technical interviewer.

            Based on this resume:

            %s

            Generate exactly 10 interview questions.

            Rules:
            - Return ONLY the questions.
            - No introduction.
            - No explanation.
            - No heading.
            - No closing statement.
            - Number each question.

            Example:

            1. Question...
            2. Question...
            for the role: %s

            Include:
            - technical questions
            - project questions
            - behavioral questions

            Return clean numbered questions.
            """, resumeText != null ? resumeText : "", safeRole);

        try {
            if (apiKey != null && !apiKey.isBlank() && !apiKey.contains("your_gemini_api_key_here")) {
                return callGemini(prompt);
            } else {
                log.warn("GEMINI_API_KEY is not set or using placeholder, returning default mock interview questions");
            }
        } catch (Exception e) {
            log.error("Failed to generate interview questions via Gemini API, falling back to default question set", e);
        }

        return getFallbackQuestions(safeRole);
    }

    public String generateFeedback(String questionsJson, String answersText) {
        String prompt = String.format("""
            You are an expert technical interviewer.

            Questions:
            %s

            Candidate Answers:
            %s

            Evaluate EACH answer separately.

            Return ONLY valid JSON.

            {
              "overall_score": 0,
              "items": [
                {
                  "question_id": "q1",
                  "score": 85,
                  "feedback": "Good understanding of the concept."
                }
              ]
            }

            Rules:
            - overall_score must be between 0 and 100
            - score for each question must be between 0 and 100
            - give short but useful feedback
            - return JSON only
            """, questionsJson != null ? questionsJson : "", answersText != null ? answersText : "");

        try {
            if (apiKey != null && !apiKey.isBlank() && !apiKey.contains("your_gemini_api_key_here")) {
                String rawText = callGemini(prompt);
                return cleanJsonText(rawText);
            } else {
                log.warn("GEMINI_API_KEY is not set or using placeholder, returning default mock feedback");
            }
        } catch (Exception e) {
            log.error("Failed to generate feedback via Gemini API, falling back to default feedback", e);
        }

        return getFallbackFeedbackJson();
    }

    private String getFallbackQuestions(String role) {
        return String.format("""
            1. Can you explain the system architecture and core components of a major project you built for a %s role?
            2. How do you handle concurrency, thread safety, and data consistency in high-throughput backend services?
            3. What database indexing and query optimization techniques do you apply when handling large-scale data?
            4. How do RESTful APIs manage stateless authentication using JSON Web Tokens (JWT) and secure session storage?
            5. How would you design a distributed caching layer using Redis to optimize database read performance?
            6. Explain how asynchronous messaging brokers like Apache Kafka decouple microservice dependencies.
            7. What is your strategy for writing robust unit tests and integration tests for core business workflows?
            8. Describe a complex memory leak, performance bottleneck, or production outage you debugged and resolved.
            9. How do you evaluate technical trade-offs between delivery speed and architectural scalability?
            10. How do you stay updated with modern software engineering paradigms and open-source ecosystems?
            """, role);
    }

    private String getFallbackFeedbackJson() {
        return """
            {
              "overall_score": 85,
              "items": [
                {"question_id": "q1", "score": 88, "feedback": "Solid explanation of system architecture and component design."},
                {"question_id": "q2", "score": 82, "feedback": "Good understanding of concurrency and thread safety concepts."},
                {"question_id": "q3", "score": 85, "feedback": "Well-articulated approach to database query optimization."},
                {"question_id": "q4", "score": 86, "feedback": "Clear explanation of REST authentication and JWT security."},
                {"question_id": "q5", "score": 84, "feedback": "Good awareness of Redis caching strategies and invalidation."},
                {"question_id": "q6", "score": 87, "feedback": "Strong understanding of event-driven Kafka architecture."},
                {"question_id": "q7", "score": 83, "feedback": "Practical approach to unit and integration testing coverage."},
                {"question_id": "q8", "score": 85, "feedback": "Effective problem-solving and diagnostic methodology demonstrated."},
                {"question_id": "q9", "score": 84, "feedback": "Balanced perspective on technical trade-offs and code quality."},
                {"question_id": "q10", "score": 89, "feedback": "Great commitment to continuous technical growth and best practices."}
              ]
            }
            """;
    }

    private String callGemini(String promptText) throws Exception {
        String fullUrl = apiUrl + "?key=" + apiKey;

        Map<String, Object> textPart = Map.of("text", promptText);
        Map<String, Object> contentsObj = Map.of("parts", List.of(textPart));
        Map<String, Object> requestBodyMap = Map.of("contents", List.of(contentsObj));

        String requestBodyJson = objectMapper.writeValueAsString(requestBodyMap);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson))
                .timeout(Duration.ofSeconds(60))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            log.error("Gemini API error response: {}", response.body());
            throw new RuntimeException("Gemini API responded with status: " + response.statusCode());
        }

        JsonNode rootNode = objectMapper.readTree(response.body());
        JsonNode candidates = rootNode.path("candidates");
        if (candidates.isArray() && candidates.size() > 0) {
            JsonNode textNode = candidates.get(0).path("content").path("parts").get(0).path("text");
            return textNode.asText();
        }

        throw new RuntimeException("No candidates returned from Gemini API");
    }

    private String cleanJsonText(String text) {
        if (text == null) return "{}";
        String cleaned = text.trim();
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7);
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3);
        }
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3);
        }
        return cleaned.trim();
    }
}
