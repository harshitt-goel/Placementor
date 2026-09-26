package com.placementor.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ProgressSummaryDto {
    @JsonProperty("completed_task_ids")
    private List<String> completedTaskIds;

    @JsonProperty("completed_tasks")
    private Integer completedTasks;

    @JsonProperty("total_tasks")
    private Integer totalTasks;

    private Integer percentage;

    public ProgressSummaryDto() {}

    public ProgressSummaryDto(List<String> completedTaskIds, Integer completedTasks, Integer totalTasks, Integer percentage) {
        this.completedTaskIds = completedTaskIds;
        this.completedTasks = completedTasks;
        this.totalTasks = totalTasks;
        this.percentage = percentage;
    }

    public static ProgressSummaryDtoBuilder builder() {
        return new ProgressSummaryDtoBuilder();
    }

    public List<String> getCompletedTaskIds() { return completedTaskIds; }
    public void setCompletedTaskIds(List<String> completedTaskIds) { this.completedTaskIds = completedTaskIds; }
    public Integer getCompletedTasks() { return completedTasks; }
    public void setCompletedTasks(Integer completedTasks) { this.completedTasks = completedTasks; }
    public Integer getTotalTasks() { return totalTasks; }
    public void setTotalTasks(Integer totalTasks) { this.totalTasks = totalTasks; }
    public Integer getPercentage() { return percentage; }
    public void setPercentage(Integer percentage) { this.percentage = percentage; }

    public static class ProgressSummaryDtoBuilder {
        private List<String> completedTaskIds;
        private Integer completedTasks;
        private Integer totalTasks;
        private Integer percentage;

        public ProgressSummaryDtoBuilder completedTaskIds(List<String> completedTaskIds) { this.completedTaskIds = completedTaskIds; return this; }
        public ProgressSummaryDtoBuilder completedTasks(Integer completedTasks) { this.completedTasks = completedTasks; return this; }
        public ProgressSummaryDtoBuilder totalTasks(Integer totalTasks) { this.totalTasks = totalTasks; return this; }
        public ProgressSummaryDtoBuilder percentage(Integer percentage) { this.percentage = percentage; return this; }

        public ProgressSummaryDto build() {
            return new ProgressSummaryDto(completedTaskIds, completedTasks, totalTasks, percentage);
        }
    }
}
