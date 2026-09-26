package com.placementor.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "resumes")
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "extracted_text", columnDefinition = "TEXT")
    private String extractedText;

    public Resume() {}

    public Resume(Long id, Long userId, String extractedText) {
        this.id = id;
        this.userId = userId;
        this.extractedText = extractedText;
    }

    public static ResumeBuilder builder() {
        return new ResumeBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getExtractedText() { return extractedText; }
    public void setExtractedText(String extractedText) { this.extractedText = extractedText; }

    public static class ResumeBuilder {
        private Long id;
        private Long userId;
        private String extractedText;

        public ResumeBuilder id(Long id) { this.id = id; return this; }
        public ResumeBuilder userId(Long userId) { this.userId = userId; return this; }
        public ResumeBuilder extractedText(String extractedText) { this.extractedText = extractedText; return this; }
        public Resume build() { return new Resume(id, userId, extractedText); }
    }
}
