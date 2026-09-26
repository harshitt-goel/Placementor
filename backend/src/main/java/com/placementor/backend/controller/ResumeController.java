package com.placementor.backend.controller;

import com.placementor.backend.security.UserPrincipal;
import com.placementor.backend.service.ResumeService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/resume")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping("/upload")
    public Map<String, Object> uploadResume(@AuthenticationPrincipal UserPrincipal principal,
                                            @RequestParam("file") MultipartFile file) {
        return resumeService.uploadResume(principal.getId(), file);
    }

    @GetMapping({"", "/"})
    public Map<String, Object> getResume(@AuthenticationPrincipal UserPrincipal principal) {
        return resumeService.getResume(principal.getId());
    }
}
