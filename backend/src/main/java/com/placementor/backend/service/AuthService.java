package com.placementor.backend.service;

import com.placementor.backend.dto.AuthResponseDto;
import com.placementor.backend.dto.UserCreateDto;
import com.placementor.backend.dto.UserLoginDto;
import com.placementor.backend.dto.UserResponseDto;
import com.placementor.backend.entity.User;
import com.placementor.backend.repository.UserRepository;
import com.placementor.backend.security.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponseDto signup(UserCreateDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already registered");
        }

        User user = User.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .build();

        User savedUser = userRepository.save(user);
        String token = tokenProvider.generateToken(savedUser.getEmail());

        return AuthResponseDto.builder()
                .user(new UserResponseDto(savedUser.getId(), savedUser.getEmail()))
                .accessToken(token)
                .tokenType("bearer")
                .build();
    }

    public AuthResponseDto login(UserLoginDto dto) {
        String email = dto.getEffectiveEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid password");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, dto.getPassword())
        );

        String token = tokenProvider.generateToken(user.getEmail());

        return AuthResponseDto.builder()
                .user(new UserResponseDto(user.getId(), user.getEmail()))
                .accessToken(token)
                .tokenType("bearer")
                .build();
    }
}
