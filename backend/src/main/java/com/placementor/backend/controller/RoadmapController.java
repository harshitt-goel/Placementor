package com.placementor.backend.controller;

import com.placementor.backend.security.UserPrincipal;
import com.placementor.backend.service.RoadmapService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/roadmap")
public class RoadmapController {

    private final RoadmapService roadmapService;

    public RoadmapController(RoadmapService roadmapService) {
        this.roadmapService = roadmapService;
    }

    @GetMapping({"", "/"})
    public Map<String, Object> getRoadmap(@AuthenticationPrincipal UserPrincipal principal) {
        return roadmapService.getRoadmap(principal.getId());
    }

    @PostMapping("/generate")
    public Map<String, Object> generateRoadmap(@AuthenticationPrincipal UserPrincipal principal) {
        return roadmapService.generateRoadmap(principal.getId());
    }
}
