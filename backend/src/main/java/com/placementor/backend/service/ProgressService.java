package com.placementor.backend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.placementor.backend.dto.ProgressDashboardDto;
import com.placementor.backend.dto.ProgressSummaryDto;
import com.placementor.backend.dto.TaskCompleteDto;
import com.placementor.backend.entity.Progress;
import com.placementor.backend.entity.Roadmap;
import com.placementor.backend.repository.ProgressRepository;
import com.placementor.backend.repository.RoadmapRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class ProgressService {

    private static final Logger log = LoggerFactory.getLogger(ProgressService.class);

    private final ProgressRepository progressRepository;
    private final RoadmapRepository roadmapRepository;
    private final ObjectMapper objectMapper;

    public ProgressService(ProgressRepository progressRepository, RoadmapRepository roadmapRepository, ObjectMapper objectMapper) {
        this.progressRepository = progressRepository;
        this.roadmapRepository = roadmapRepository;
        this.objectMapper = objectMapper;
    }

    public Map<String, String> completeTask(Long userId, TaskCompleteDto request) {
        Roadmap roadmap = roadmapRepository.findTopByUserIdOrderByIdDesc(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Roadmap not found"));

        Optional<Progress> existing = progressRepository.findByUserIdAndTaskName(userId, request.getTaskName());
        if (existing.isPresent()) {
            Progress p = existing.get();
            p.setCompleted(true);
            progressRepository.save(p);
        } else {
            Progress p = Progress.builder()
                    .userId(userId)
                    .role(roadmap.getRole())
                    .taskName(request.getTaskName())
                    .completed(true)
                    .build();
            progressRepository.save(p);
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "Task marked as completed");
        return response;
    }

    public Map<String, String> uncompleteTask(Long userId, TaskCompleteDto request) {
        Progress task = progressRepository.findByUserIdAndTaskName(userId, request.getTaskName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        task.setCompleted(false);
        progressRepository.save(task);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Task marked as incomplete");
        return response;
    }

    public ProgressSummaryDto getProgress(Long userId) {
        Roadmap roadmap = roadmapRepository.findTopByUserIdOrderByIdDesc(userId).orElse(null);
        if (roadmap == null) {
            return null;
        }

        int totalTasks = calculateTotalTasks(roadmap.getRoadmapData());
        List<Progress> completedList = progressRepository.findByUserIdAndCompletedTrue(userId);

        List<String> completedTaskNames = new ArrayList<>();
        for (Progress p : completedList) {
            completedTaskNames.add(p.getTaskName());
        }

        int completedCount = completedTaskNames.size();
        int percentage = totalTasks > 0 ? (int) Math.round(((double) completedCount / totalTasks) * 100) : 0;

        return ProgressSummaryDto.builder()
                .completedTaskIds(completedTaskNames)
                .completedTasks(completedCount)
                .totalTasks(totalTasks)
                .percentage(percentage)
                .build();
    }

    public ProgressDashboardDto getDashboard(Long userId) {
        Roadmap roadmap = roadmapRepository.findTopByUserIdOrderByIdDesc(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Roadmap not found"));

        int totalTasks = calculateTotalTasks(roadmap.getRoadmapData());
        long completedTasks = progressRepository.countByUserIdAndCompletedTrue(userId);

        double percentage = totalTasks > 0 ? ((double) completedTasks / totalTasks) * 100 : 0.0;
        double roundedPercentage = Math.round(percentage * 100.0) / 100.0;

        return ProgressDashboardDto.builder()
                .role(roadmap.getRole())
                .totalTasks(totalTasks)
                .completedTasks((int) completedTasks)
                .progressPercentage(roundedPercentage)
                .build();
    }

    @SuppressWarnings("unchecked")
    private int calculateTotalTasks(String roadmapDataJson) {
        if (roadmapDataJson == null || roadmapDataJson.isBlank()) return 0;
        try {
            Map<String, Object> data = objectMapper.readValue(roadmapDataJson, new TypeReference<>() {});
            List<Map<String, Object>> phases = (List<Map<String, Object>>) data.getOrDefault("phases", Collections.emptyList());
            int total = 0;
            for (Map<String, Object> phase : phases) {
                List<?> tasks = (List<?>) phase.getOrDefault("tasks", Collections.emptyList());
                total += tasks.size();
            }
            return total;
        } catch (Exception e) {
            log.error("Error parsing total tasks from roadmap data", e);
            return 0;
        }
    }
}
