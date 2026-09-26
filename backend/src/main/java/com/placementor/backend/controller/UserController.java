package com.placementor.backend.controller;

import com.placementor.backend.dto.UserResponseDto;
import com.placementor.backend.security.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class UserController {

    @GetMapping({"/me", "/users/me"})
    public UserResponseDto getMe(@AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        return UserResponseDto.builder()
                .id(principal.getId())
                .email(principal.getEmail())
                .build();
    }
}
