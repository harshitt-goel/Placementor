package com.placementor.backend.dto;

public class InterviewFeedbackEvent {
    private Long interviewId;
    private String questionsJson;
    private String answerText;
    private String answersJson;

    public InterviewFeedbackEvent() {}

    public InterviewFeedbackEvent(Long interviewId, String questionsJson, String answerText, String answersJson) {
        this.interviewId = interviewId;
        this.questionsJson = questionsJson;
        this.answerText = answerText;
        this.answersJson = answersJson;
    }

    public static InterviewFeedbackEventBuilder builder() {
        return new InterviewFeedbackEventBuilder();
    }

    public Long getInterviewId() { return interviewId; }
    public void setInterviewId(Long interviewId) { this.interviewId = interviewId; }
    public String getQuestionsJson() { return questionsJson; }
    public void setQuestionsJson(String questionsJson) { this.questionsJson = questionsJson; }
    public String getAnswerText() { return answerText; }
    public void setAnswerText(String answerText) { this.answerText = answerText; }
    public String getAnswersJson() { return answersJson; }
    public void setAnswersJson(String answersJson) { this.answersJson = answersJson; }

    public static class InterviewFeedbackEventBuilder {
        private Long interviewId;
        private String questionsJson;
        private String answerText;
        private String answersJson;

        public InterviewFeedbackEventBuilder interviewId(Long interviewId) { this.interviewId = interviewId; return this; }
        public InterviewFeedbackEventBuilder questionsJson(String questionsJson) { this.questionsJson = questionsJson; return this; }
        public InterviewFeedbackEventBuilder answerText(String answerText) { this.answerText = answerText; return this; }
        public InterviewFeedbackEventBuilder answersJson(String answersJson) { this.answersJson = answersJson; return this; }

        public InterviewFeedbackEvent build() {
            return new InterviewFeedbackEvent(interviewId, questionsJson, answerText, answersJson);
        }
    }
}
