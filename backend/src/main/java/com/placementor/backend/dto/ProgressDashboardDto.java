package com.placementor.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
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

    @JsonProperty("total_tasks")
    public Integer getTotalTasks() { return totalTasks; }
    @JsonProperty("total_tasks")
    public void setTotalTasks(Integer totalTasks) { this.totalTasks = totalTasks; }

    @JsonProperty("completed_tasks")
    public Integer getCompletedTasks() { return completedTasks; }
    @JsonProperty("completed_tasks")
    public void setCompletedTasks(Integer completedTasks) { this.completedTasks = completedTasks; }

    @JsonProperty("progress_percentage")
    public Double getProgressPercentage() { return progressPercentage; }
    @JsonProperty("progress_percentage")
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
