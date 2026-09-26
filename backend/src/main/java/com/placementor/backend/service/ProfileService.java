package com.placementor.backend.service;

import com.placementor.backend.dto.ProfileDto;
import com.placementor.backend.entity.Profile;
import com.placementor.backend.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final CacheManager cacheManager;

    public ProfileService(ProfileRepository profileRepository, @Autowired(required = false) CacheManager cacheManager) {
        this.profileRepository = profileRepository;
        this.cacheManager = cacheManager;
    }

    @Cacheable(value = "profiles", key = "#userId", unless = "#result == null")
    public ProfileDto getProfile(Long userId) {
        Profile profile = profileRepository.findTopByUserIdOrderByIdDesc(userId).orElse(null);
        if (profile == null) {
            return null;
        }
        return mapToDto(profile);
    }

    public ProfileDto createProfile(Long userId, ProfileDto dto) {
        return saveOrUpdateProfile(userId, dto);
    }

    public ProfileDto updateProfile(Long userId, ProfileDto dto) {
        return saveOrUpdateProfile(userId, dto);
    }

    private ProfileDto saveOrUpdateProfile(Long userId, ProfileDto dto) {
        Profile profile = profileRepository.findTopByUserIdOrderByIdDesc(userId).orElse(null);
        if (profile == null) {
            profile = Profile.builder()
                    .userId(userId)
                    .build();
        }

        profile.setTargetRole(dto.getTargetRole());
        profile.setDomain(dto.getDomain());
        profile.setCurrentLevel(dto.getCurrentLevel());
        profile.setGithubUrl(dto.getGithubUrl());
        profile.setLeetcodeUrl(dto.getLeetcodeUrl());
        profile.setCodeforcesUrl(dto.getCodeforcesUrl());
        profile.setTargetCompany(dto.getTargetCompany());

        Profile saved = profileRepository.save(profile);

        if (cacheManager != null && cacheManager.getCache("profiles") != null) {
            cacheManager.getCache("profiles").evict(userId);
        }

        return mapToDto(saved);
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
