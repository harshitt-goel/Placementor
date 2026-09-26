package com.placementor.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class InterviewFeedbackDto {
    @JsonProperty("overall_score")
    private Integer overallScore;

    private String status;

    private List<FeedbackItemDto> items;

    public InterviewFeedbackDto() {}

    public InterviewFeedbackDto(Integer overallScore, String status, List<FeedbackItemDto> items) {
        this.overallScore = overallScore;
        this.status = status;
        this.items = items;
    }

    public static InterviewFeedbackDtoBuilder builder() {
        return new InterviewFeedbackDtoBuilder();
    }

    @JsonProperty("overall_score")
    public Integer getOverallScore() { return overallScore; }
    @JsonProperty("overall_score")
    public void setOverallScore(Integer overallScore) { this.overallScore = overallScore; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<FeedbackItemDto> getItems() { return items; }
    public void setItems(List<FeedbackItemDto> items) { this.items = items; }

    public static class InterviewFeedbackDtoBuilder {
        private Integer overallScore;
        private String status;
        private List<FeedbackItemDto> items;

        public InterviewFeedbackDtoBuilder overallScore(Integer overallScore) { this.overallScore = overallScore; return this; }
        public InterviewFeedbackDtoBuilder status(String status) { this.status = status; return this; }
        public InterviewFeedbackDtoBuilder items(List<FeedbackItemDto> items) { this.items = items; return this; }

        public InterviewFeedbackDto build() {
            return new InterviewFeedbackDto(overallScore, status, items);
        }
    }
}
