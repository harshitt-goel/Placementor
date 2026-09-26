package com.placementor.backend.dto;

public class InterviewQuestionEvent {
    private Long interviewId;
    private String resumeText;
    private String role;

    public InterviewQuestionEvent() {}

    public InterviewQuestionEvent(Long interviewId, String resumeText, String role) {
        this.interviewId = interviewId;
        this.resumeText = resumeText;
        this.role = role;
    }

    public static InterviewQuestionEventBuilder builder() {
        return new InterviewQuestionEventBuilder();
    }

    public Long getInterviewId() { return interviewId; }
    public void setInterviewId(Long interviewId) { this.interviewId = interviewId; }
    public String getResumeText() { return resumeText; }
    public void setResumeText(String resumeText) { this.resumeText = resumeText; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public static class InterviewQuestionEventBuilder {
        private Long interviewId;
        private String resumeText;
        private String role;

        public InterviewQuestionEventBuilder interviewId(Long interviewId) { this.interviewId = interviewId; return this; }
        public InterviewQuestionEventBuilder resumeText(String resumeText) { this.resumeText = resumeText; return this; }
        public InterviewQuestionEventBuilder role(String role) { this.role = role; return this; }

        public InterviewQuestionEvent build() {
            return new InterviewQuestionEvent(interviewId, resumeText, role);
        }
    }
}
