package com.placementor.backend.dto;

public class InterviewGenerateDto {
    private String role;

    public InterviewGenerateDto() {}

    public InterviewGenerateDto(String role) {
        this.role = role;
    }

    public static InterviewGenerateDtoBuilder builder() {
        return new InterviewGenerateDtoBuilder();
    }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public static class InterviewGenerateDtoBuilder {
        private String role;

        public InterviewGenerateDtoBuilder role(String role) { this.role = role; return this; }
        public InterviewGenerateDto build() { return new InterviewGenerateDto(role); }
    }
}
