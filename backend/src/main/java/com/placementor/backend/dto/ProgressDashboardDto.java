package com.placementor.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProgressDashboardDto {
    private String role;

    @JsonProperty("total_tasks")
    private Integer totalTasks;

    @JsonProperty("completed_tasks")
    private Integer completedTasks;

    @JsonProperty("progress_percentage")
    private Double progressPercentage;

    public ProgressDashboardDto() {}

    public ProgressDashboardDto(String role, Integer totalTasks, Integer completedTasks, Double progressPercentage) {
        this.role = role;
        this.totalTasks = totalTasks;
        this.completedTasks = completedTasks;
        this.progressPercentage = progressPercentage;
    }

    public static ProgressDashboardDtoBuilder builder() {
        return new ProgressDashboardDtoBuilder();
    }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Integer getTotalTasks() { return totalTasks; }
    public void setTotalTasks(Integer totalTasks) { this.totalTasks = totalTasks; }
    public Integer getCompletedTasks() { return completedTasks; }
    public void setCompletedTasks(Integer completedTasks) { this.completedTasks = completedTasks; }
    public Double getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(Double progressPercentage) { this.progressPercentage = progressPercentage; }

    public static class ProgressDashboardDtoBuilder {
        private String role;
        private Integer totalTasks;
        private Integer completedTasks;
        private Double progressPercentage;

        public ProgressDashboardDtoBuilder role(String role) { this.role = role; return this; }
        public ProgressDashboardDtoBuilder totalTasks(Integer totalTasks) { this.totalTasks = totalTasks; return this; }
        public ProgressDashboardDtoBuilder completedTasks(Integer completedTasks) { this.completedTasks = completedTasks; return this; }
        public ProgressDashboardDtoBuilder progressPercentage(Double progressPercentage) { this.progressPercentage = progressPercentage; return this; }

        public ProgressDashboardDto build() {
            return new ProgressDashboardDto(role, totalTasks, completedTasks, progressPercentage);
        }
    }
}
