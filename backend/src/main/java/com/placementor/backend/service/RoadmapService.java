package com.placementor.backend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.placementor.backend.entity.Profile;
import com.placementor.backend.entity.Roadmap;
import com.placementor.backend.repository.ProfileRepository;
import com.placementor.backend.repository.ProgressRepository;
import com.placementor.backend.repository.RoadmapRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.InputStream;
import java.util.*;

@Service
public class RoadmapService {

    private static final Logger log = LoggerFactory.getLogger(RoadmapService.class);

    private final RoadmapRepository roadmapRepository;
    private final ProfileRepository profileRepository;
    private final ProgressRepository progressRepository;
    private final ObjectMapper objectMapper;
    private final CacheManager cacheManager;

    public RoadmapService(RoadmapRepository roadmapRepository,
                          ProfileRepository profileRepository,
                          ProgressRepository progressRepository,
                          ObjectMapper objectMapper,
                          @Autowired(required = false) CacheManager cacheManager) {
        this.roadmapRepository = roadmapRepository;
        this.profileRepository = profileRepository;
        this.progressRepository = progressRepository;
        this.objectMapper = objectMapper;
        this.cacheManager = cacheManager;
    }

    @Cacheable(value = "roadmaps", key = "#userId", unless = "#result == null")
    public Map<String, Object> getRoadmap(Long userId) {
        Roadmap roadmap = roadmapRepository.findTopByUserIdOrderByIdDesc(userId).orElse(null);

        if (roadmap == null) {
            Profile profile = profileRepository.findTopByUserIdOrderByIdDesc(userId).orElse(null);
            if (profile != null && profile.getTargetRole() != null && !profile.getTargetRole().isBlank()) {
                try {
                    return generateRoadmap(userId);
                } catch (Exception e) {
                    log.error("Auto generation of roadmap in getRoadmap failed for userId {}", userId, e);
                }
            }
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Roadmap not found");
        }

        try {
            Map<String, Object> roadmapData = objectMapper.readValue(roadmap.getRoadmapData(), new TypeReference<>() {});
            Map<String, Object> formatted = formatRoadmap(roadmapData);

            Profile profile = profileRepository.findTopByUserIdOrderByIdDesc(userId).orElse(null);
            if (profile != null && profile.getTargetRole() != null) {
                formatted.put("current_profile_role", profile.getTargetRole());
                boolean needsRegen = !roadmap.getRole().trim().equalsIgnoreCase(profile.getTargetRole().trim());
                formatted.put("needs_regeneration", needsRegen);
            }

            return formatted;
        } catch (Exception e) {
            log.error("Failed to parse roadmap JSON data", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error reading roadmap data");
        }
    }

    @Transactional
    public Map<String, Object> generateRoadmap(Long userId) {
        Profile profile = profileRepository.findTopByUserIdOrderByIdDesc(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));

        String rawRole = profile.getTargetRole() != null ? profile.getTargetRole().trim() : "Software Development Engineer (SDE)";
        String roleLower = rawRole.toLowerCase();
        String resourcePath;

        if (roleLower.contains("backend")) {
            resourcePath = "roadmaps/backend_developer.json";
        } else {
            resourcePath = "roadmaps/sde.json";
        }

        try {
            ClassPathResource resource = new ClassPathResource(resourcePath);
            InputStream inputStream = resource.getInputStream();
            Map<String, Object> roadmapJson = objectMapper.readValue(inputStream, new TypeReference<>() {});

            roadmapJson.put("role", rawRole);

            roadmapRepository.deleteByUserId(userId);
            progressRepository.deleteByUserId(userId);

            String jsonString = objectMapper.writeValueAsString(roadmapJson);
            Roadmap newRoadmap = Roadmap.builder()
                    .userId(userId)
                    .role(rawRole)
                    .roadmapData(jsonString)
                    .build();

            roadmapRepository.save(newRoadmap);

            if (cacheManager != null && cacheManager.getCache("roadmaps") != null) {
                cacheManager.getCache("roadmaps").evict(userId);
            }

            return formatRoadmap(roadmapJson);
        } catch (Exception e) {
            log.error("Error reading or saving roadmap resource for userId {}", userId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate roadmap: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> formatRoadmap(Map<String, Object> data) {
        List<Map<String, Object>> formattedPhases = new ArrayList<>();
        List<Map<String, Object>> phases = (List<Map<String, Object>>) data.getOrDefault("phases", Collections.emptyList());

        for (int i = 0; i < phases.size(); i++) {
            Map<String, Object> phase = phases.get(i);
            int phaseIdx = i + 1;

            List<String> tasks = (List<String>) phase.getOrDefault("tasks", Collections.emptyList());
            List<Map<String, Object>> formattedTasks = new ArrayList<>();

            for (int t = 0; t < tasks.size(); t++) {
                int taskIdx = t + 1;
                Map<String, Object> taskMap = new HashMap<>();
                taskMap.put("id", "phase-" + phaseIdx + "-task-" + taskIdx);
                taskMap.put("title", tasks.get(t));
                taskMap.put("description", "");
                formattedTasks.add(taskMap);
            }

            Map<String, Object> phaseMap = new HashMap<>();
            phaseMap.put("phase_number", phaseIdx);
            phaseMap.put("title", phase.getOrDefault("phase", ""));
            phaseMap.put("description", "");
            phaseMap.put("tasks", formattedTasks);

            formattedPhases.add(phaseMap);
        }

        Map<String, Object> result = new HashMap<>();
        String roleStr = (String) data.getOrDefault("role", "");
        result.put("role", roleStr);
        result.put("phases", formattedPhases);
        result.put("needs_regeneration", false);
        result.put("current_profile_role", roleStr);

        return result;
    }
}
