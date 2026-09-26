package com.placementor.backend.controller;

import com.placementor.backend.dto.AuthResponseDto;
import com.placementor.backend.dto.UserCreateDto;
import com.placementor.backend.dto.UserLoginDto;
import com.placementor.backend.service.AuthService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping({"/signup", "/auth/signup"})
    public AuthResponseDto signup(@RequestBody UserCreateDto dto) {
        return authService.signup(dto);
    }

    @PostMapping(value = {"/login", "/auth/login"}, consumes = MediaType.APPLICATION_JSON_VALUE)
    public AuthResponseDto loginJson(@RequestBody UserLoginDto dto) {
        return authService.login(dto);
    }

    @PostMapping(value = {"/login", "/auth/login"}, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public AuthResponseDto loginForm(UserLoginDto dto) {
        return authService.login(dto);
    }
}
