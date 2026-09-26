package com.placementor.backend.controller;

import com.placementor.backend.dto.UserResponseDto;
import com.placementor.backend.security.UserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping({"/me", "/users/me"})
    public UserResponseDto getMe(@AuthenticationPrincipal UserPrincipal principal) {
        return UserResponseDto.builder()
                .id(principal.getId())
                .email(principal.getEmail())
                .build();
    }
}
