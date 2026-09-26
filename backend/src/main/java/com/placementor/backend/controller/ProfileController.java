package com.placementor.backend.controller;

import com.placementor.backend.dto.ProfileDto;
import com.placementor.backend.security.UserPrincipal;
import com.placementor.backend.service.ProfileService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping({"", "/"})
    public ProfileDto getProfile(@AuthenticationPrincipal UserPrincipal principal) {
        return profileService.getProfile(principal.getId());
    }

    @PostMapping({"", "/"})
    public ProfileDto createProfile(@AuthenticationPrincipal UserPrincipal principal, @RequestBody ProfileDto dto) {
        return profileService.createProfile(principal.getId(), dto);
    }

    @PutMapping({"", "/"})
    public ProfileDto updateProfile(@AuthenticationPrincipal UserPrincipal principal, @RequestBody ProfileDto dto) {
        return profileService.updateProfile(principal.getId(), dto);
    }
}
