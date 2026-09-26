package com.placementor.backend.controller;

import com.placementor.backend.dto.ProfileDto;
import com.placementor.backend.security.UserPrincipal;
import com.placementor.backend.service.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping({"", "/"})
    public ProfileDto getProfile(@AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        ProfileDto dto = profileService.getProfile(principal.getId());
        return dto != null ? dto : new ProfileDto();
    }

    @PostMapping({"", "/"})
    public ProfileDto createProfile(@AuthenticationPrincipal UserPrincipal principal, @RequestBody ProfileDto dto) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        return profileService.createProfile(principal.getId(), dto);
    }

    @PutMapping({"", "/"})
    public ProfileDto updateProfile(@AuthenticationPrincipal UserPrincipal principal, @RequestBody ProfileDto dto) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        return profileService.updateProfile(principal.getId(), dto);
    }
}
