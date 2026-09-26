package com.placementor.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import java.util.List;
import java.util.Map;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class InterviewResponseDto {
    private Long id;
    private String role;
    private List<Map<String, Object>> questions;
    private String status;
    private Boolean submitted;

    @JsonProperty("created_at")
    private String createdAt;

    public InterviewResponseDto() {}

    public InterviewResponseDto(Long id, String role, List<Map<String, Object>> questions, String status, Boolean submitted, String createdAt) {
        this.id = id;
        this.role = role;
        this.questions = questions;
        this.status = status;
        this.submitted = submitted;
        this.createdAt = createdAt;
    }

    public static InterviewResponseDtoBuilder builder() {
        return new InterviewResponseDtoBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public List<Map<String, Object>> getQuestions() { return questions; }
    public void setQuestions(List<Map<String, Object>> questions) { this.questions = questions; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Boolean getSubmitted() { return submitted; }
    public void setSubmitted(Boolean submitted) { this.submitted = submitted; }

    @JsonProperty("created_at")
    public String getCreatedAt() { return createdAt; }
    @JsonProperty("created_at")
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public static class InterviewResponseDtoBuilder {
        private Long id;
        private String role;
        private List<Map<String, Object>> questions;
        private String status;
        private Boolean submitted;
        private String createdAt;

        public InterviewResponseDtoBuilder id(Long id) { this.id = id; return this; }
        public InterviewResponseDtoBuilder role(String role) { this.role = role; return this; }
        public InterviewResponseDtoBuilder questions(List<Map<String, Object>> questions) { this.questions = questions; return this; }
        public InterviewResponseDtoBuilder status(String status) { this.status = status; return this; }
        public InterviewResponseDtoBuilder submitted(Boolean submitted) { this.submitted = submitted; return this; }
        public InterviewResponseDtoBuilder createdAt(String createdAt) { this.createdAt = createdAt; return this; }

        public InterviewResponseDto build() {
            return new InterviewResponseDto(id, role, questions, status, submitted, createdAt);
        }
    }
}
