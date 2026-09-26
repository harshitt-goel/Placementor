package com.placementor.backend.controller;

import com.placementor.backend.dto.ProgressDashboardDto;
import com.placementor.backend.dto.ProgressSummaryDto;
import com.placementor.backend.dto.TaskCompleteDto;
import com.placementor.backend.security.UserPrincipal;
import com.placementor.backend.service.ProgressService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/progress")
public class ProgressController {

    private final ProgressService progressService;

    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    @PostMapping("/complete")
    public Map<String, String> completeTask(@AuthenticationPrincipal UserPrincipal principal,
                                            @RequestBody TaskCompleteDto request) {
        return progressService.completeTask(principal.getId(), request);
    }

    @PostMapping("/uncomplete")
    public Map<String, String> uncompleteTask(@AuthenticationPrincipal UserPrincipal principal,
                                              @RequestBody TaskCompleteDto request) {
        return progressService.uncompleteTask(principal.getId(), request);
    }

    @GetMapping({"", "/"})
    public ProgressSummaryDto getProgress(@AuthenticationPrincipal UserPrincipal principal) {
        return progressService.getProgress(principal.getId());
    }

    @GetMapping("/dashboard")
    public ProgressDashboardDto getDashboard(@AuthenticationPrincipal UserPrincipal principal) {
        return progressService.getDashboard(principal.getId());
    }
}
