package com.placementor.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class InterviewSubmitDto {
    private List<AnswerItem> answers;

    public InterviewSubmitDto() {}

    public InterviewSubmitDto(List<AnswerItem> answers) {
        this.answers = answers;
    }

    public static InterviewSubmitDtoBuilder builder() {
        return new InterviewSubmitDtoBuilder();
    }

    public List<AnswerItem> getAnswers() { return answers; }
    public void setAnswers(List<AnswerItem> answers) { this.answers = answers; }

    public static class InterviewSubmitDtoBuilder {
        private List<AnswerItem> answers;

        public InterviewSubmitDtoBuilder answers(List<AnswerItem> answers) { this.answers = answers; return this; }
        public InterviewSubmitDto build() { return new InterviewSubmitDto(answers); }
    }

    public static class AnswerItem {
        @JsonProperty("question_id")
        private String questionId;
        private String answer;

        public AnswerItem() {}

        public AnswerItem(String questionId, String answer) {
            this.questionId = questionId;
            this.answer = answer;
        }

        public static AnswerItemBuilder builder() {
            return new AnswerItemBuilder();
        }

        public String getQuestionId() { return questionId; }
        public void setQuestionId(String questionId) { this.questionId = questionId; }
        public String getAnswer() { return answer; }
        public void setAnswer(String answer) { this.answer = answer; }

        public static class AnswerItemBuilder {
            private String questionId;
            private String answer;

            public AnswerItemBuilder questionId(String questionId) { this.questionId = questionId; return this; }
            public AnswerItemBuilder answer(String answer) { this.answer = answer; return this; }
            public AnswerItem build() { return new AnswerItem(questionId, answer); }
        }
    }
}
