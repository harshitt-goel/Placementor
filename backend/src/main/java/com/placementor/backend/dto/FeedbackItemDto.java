package com.placementor.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class FeedbackItemDto {
    @JsonProperty("question_id")
    private String questionId;
    private String question;
    private String answer;
    private Integer score;
    private String feedback;

    public FeedbackItemDto() {}

    public FeedbackItemDto(String questionId, String question, String answer, Integer score, String feedback) {
        this.questionId = questionId;
        this.question = question;
        this.answer = answer;
        this.score = score;
        this.feedback = feedback;
    }

    public static FeedbackItemDtoBuilder builder() {
        return new FeedbackItemDtoBuilder();
    }

    @JsonProperty("question_id")
    public String getQuestionId() { return questionId; }
    @JsonProperty("question_id")
    public void setQuestionId(String questionId) { this.questionId = questionId; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public static class FeedbackItemDtoBuilder {
        private String questionId;
        private String question;
        private String answer;
        private Integer score;
        private String feedback;

        public FeedbackItemDtoBuilder questionId(String questionId) { this.questionId = questionId; return this; }
        public FeedbackItemDtoBuilder question(String question) { this.question = question; return this; }
        public FeedbackItemDtoBuilder answer(String answer) { this.answer = answer; return this; }
        public FeedbackItemDtoBuilder score(Integer score) { this.score = score; return this; }
        public FeedbackItemDtoBuilder feedback(String feedback) { this.feedback = feedback; return this; }

        public FeedbackItemDto build() {
            return new FeedbackItemDto(questionId, question, answer, score, feedback);
        }
    }
}
