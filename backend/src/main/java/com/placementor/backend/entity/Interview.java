package com.placementor.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "interviews")
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    private String role;

    @Column(columnDefinition = "TEXT")
    private String questions;

    private String status = "COMPLETED";

    @Column(columnDefinition = "TEXT")
    private String answers;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    private Integer score;

    public Interview() {}

    public Interview(Long id, Long userId, String role, String questions, String status, String answers, String feedback, Integer score) {
        this.id = id;
        this.userId = userId;
        this.role = role;
        this.questions = questions;
        this.status = status != null ? status : "COMPLETED";
        this.answers = answers;
        this.feedback = feedback;
        this.score = score;
    }

    public static InterviewBuilder builder() {
        return new InterviewBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getQuestions() { return questions; }
    public void setQuestions(String questions) { this.questions = questions; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getAnswers() { return answers; }
    public void setAnswers(String answers) { this.answers = answers; }
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public static class InterviewBuilder {
        private Long id;
        private Long userId;
        private String role;
        private String questions;
        private String status = "COMPLETED";
        private String answers;
        private String feedback;
        private Integer score;

        public InterviewBuilder id(Long id) { this.id = id; return this; }
        public InterviewBuilder userId(Long userId) { this.userId = userId; return this; }
        public InterviewBuilder role(String role) { this.role = role; return this; }
        public InterviewBuilder questions(String questions) { this.questions = questions; return this; }
        public InterviewBuilder status(String status) { this.status = status; return this; }
        public InterviewBuilder answers(String answers) { this.answers = answers; return this; }
        public InterviewBuilder feedback(String feedback) { this.feedback = feedback; return this; }
        public InterviewBuilder score(Integer score) { this.score = score; return this; }

        public Interview build() {
            return new Interview(id, userId, role, questions, status, answers, feedback, score);
        }
    }
}
