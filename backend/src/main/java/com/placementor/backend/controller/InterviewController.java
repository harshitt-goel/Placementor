package com.placementor.backend.controller;

import com.placementor.backend.dto.*;
import com.placementor.backend.security.UserPrincipal;
import com.placementor.backend.service.InterviewService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/interviews")
public class InterviewController {

    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    @PostMapping("/generate")
    public InterviewResponseDto generateInterview(@AuthenticationPrincipal UserPrincipal principal,
                                                  @RequestBody InterviewGenerateDto dto) {
        return interviewService.generateInterview(principal.getId(), dto.getRole());
    }

    @GetMapping({"", "/"})
    public List<InterviewResponseDto> getInterviews(@AuthenticationPrincipal UserPrincipal principal) {
        return interviewService.getInterviews(principal.getId());
    }

    @GetMapping("/{interviewId}")
    public InterviewResponseDto getInterview(@AuthenticationPrincipal UserPrincipal principal,
                                             @PathVariable Long interviewId) {
        return interviewService.getInterview(interviewId, principal.getId());
    }

    @PostMapping("/{interviewId}/submit")
    public Map<String, String> submitInterview(@AuthenticationPrincipal UserPrincipal principal,
                                               @PathVariable Long interviewId,
                                               @RequestBody InterviewSubmitDto dto) {
        return interviewService.submitInterview(interviewId, principal.getId(), dto);
    }

    @GetMapping("/{interviewId}/feedback")
    public InterviewFeedbackDto getFeedback(@AuthenticationPrincipal UserPrincipal principal,
                                            @PathVariable Long interviewId) {
        return interviewService.getFeedback(interviewId, principal.getId());
    }
}
