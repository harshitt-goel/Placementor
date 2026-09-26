package com.placementor.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

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

    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }

    public static class TaskCompleteDtoBuilder {
        private String taskName;

        public TaskCompleteDtoBuilder taskName(String taskName) { this.taskName = taskName; return this; }
        public TaskCompleteDto build() { return new TaskCompleteDto(taskName); }
    }
}
