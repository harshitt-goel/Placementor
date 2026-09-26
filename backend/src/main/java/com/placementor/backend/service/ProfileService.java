package com.placementor.backend.service;

import com.placementor.backend.dto.ProfileDto;
import com.placementor.backend.entity.Profile;
import com.placementor.backend.repository.ProfileRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Cacheable(value = "profiles", key = "#userId")
    public ProfileDto getProfile(Long userId) {
        Profile profile = profileRepository.findTopByUserIdOrderByIdDesc(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));

        return mapToDto(profile);
    }

    @CacheEvict(value = "profiles", key = "#userId")
    public ProfileDto createProfile(Long userId, ProfileDto dto) {
        if (profileRepository.findByUserId(userId).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Profile already exists");
        }

        Profile profile = Profile.builder()
                .userId(userId)
                .targetRole(dto.getTargetRole())
                .domain(dto.getDomain())
                .currentLevel(dto.getCurrentLevel())
                .githubUrl(dto.getGithubUrl())
                .leetcodeUrl(dto.getLeetcodeUrl())
                .codeforcesUrl(dto.getCodeforcesUrl())
                .targetCompany(dto.getTargetCompany())
                .build();

        Profile saved = profileRepository.save(profile);
        return mapToDto(saved);
    }

    @CacheEvict(value = "profiles", key = "#userId")
    public ProfileDto updateProfile(Long userId, ProfileDto dto) {
        Profile profile = profileRepository.findTopByUserIdOrderByIdDesc(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));

        profile.setTargetRole(dto.getTargetRole());
        profile.setDomain(dto.getDomain());
        profile.setCurrentLevel(dto.getCurrentLevel());
        profile.setGithubUrl(dto.getGithubUrl());
        profile.setLeetcodeUrl(dto.getLeetcodeUrl());
        profile.setCodeforcesUrl(dto.getCodeforcesUrl());
        profile.setTargetCompany(dto.getTargetCompany());

        Profile updated = profileRepository.save(profile);
        return mapToDto(updated);
    }

    private ProfileDto mapToDto(Profile profile) {
        return ProfileDto.builder()
                .id(profile.getId())
                .userId(profile.getUserId())
                .targetRole(profile.getTargetRole())
                .domain(profile.getDomain())
                .currentLevel(profile.getCurrentLevel())
                .githubUrl(profile.getGithubUrl())
                .leetcodeUrl(profile.getLeetcodeUrl())
                .codeforcesUrl(profile.getCodeforcesUrl())
                .targetCompany(profile.getTargetCompany())
                .build();
    }
}
