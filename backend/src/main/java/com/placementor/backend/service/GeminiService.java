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

    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent}")
    private String apiUrl;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generateInterviewQuestions(String resumeText, String role) {
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
            """, resumeText != null ? resumeText : "", role != null ? role : "");

        try {
            return callGemini(prompt);
        } catch (Exception e) {
            log.error("Failed to generate interview questions via Gemini API", e);
            throw new RuntimeException("Gemini API call failed: " + e.getMessage(), e);
        }
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
            String rawText = callGemini(prompt);
            return cleanJsonText(rawText);
        } catch (Exception e) {
            log.error("Failed to generate feedback via Gemini API", e);
            throw new RuntimeException("Gemini API call failed: " + e.getMessage(), e);
        }
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
