package com.placementor.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TaskCompleteDto {
    @JsonProperty("task_name")
    private String taskName;

    public TaskCompleteDto() {}

    public TaskCompleteDto(String taskName) {
        this.taskName = taskName;
    }

    public static TaskCompleteDtoBuilder builder() {
        return new TaskCompleteDtoBuilder();
    }

    @JsonProperty("task_name")
    public String getTaskName() { return taskName; }
    @JsonProperty("task_name")
    public void setTaskName(String taskName) { this.taskName = taskName; }

    public static class TaskCompleteDtoBuilder {
        private String taskName;

        public TaskCompleteDtoBuilder taskName(String taskName) { this.taskName = taskName; return this; }
        public TaskCompleteDto build() { return new TaskCompleteDto(taskName); }
    }
}
