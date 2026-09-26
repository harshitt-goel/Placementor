package com.placementor.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "progress")
public class Progress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    private String role;

    @Column(name = "task_name", nullable = false)
    private String taskName;

    private Boolean completed = false;

    public Progress() {}

    public Progress(Long id, Long userId, String role, String taskName, Boolean completed) {
        this.id = id;
        this.userId = userId;
        this.role = role;
        this.taskName = taskName;
        this.completed = completed != null ? completed : false;
    }

    public static ProgressBuilder builder() {
        return new ProgressBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    public Boolean getCompleted() { return completed; }
    public void setCompleted(Boolean completed) { this.completed = completed; }

    public static class ProgressBuilder {
        private Long id;
        private Long userId;
        private String role;
        private String taskName;
        private Boolean completed = false;

        public ProgressBuilder id(Long id) { this.id = id; return this; }
        public ProgressBuilder userId(Long userId) { this.userId = userId; return this; }
        public ProgressBuilder role(String role) { this.role = role; return this; }
        public ProgressBuilder taskName(String taskName) { this.taskName = taskName; return this; }
        public ProgressBuilder completed(Boolean completed) { this.completed = completed; return this; }

        public Progress build() {
            return new Progress(id, userId, role, taskName, completed);
        }
    }
}
