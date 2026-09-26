package com.placementor.backend.service;

import com.placementor.backend.dto.ProfileDto;
import com.placementor.backend.entity.Profile;
import com.placementor.backend.repository.ProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

    private static final Logger log = LoggerFactory.getLogger(ProfileService.class);

    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public ProfileDto getProfile(Long userId) {
        log.info("Fetching profile for userId: {}", userId);
        Profile profile = profileRepository.findTopByUserIdOrderByIdDesc(userId).orElse(null);
        if (profile == null) {
            log.info("No profile found in DB for userId: {}", userId);
            return null;
        }
        log.info("Found profile id {} for userId {}: role={}, domain={}", profile.getId(), userId, profile.getTargetRole(), profile.getDomain());
        return mapToDto(profile);
    }

    public ProfileDto createProfile(Long userId, ProfileDto dto) {
        return saveOrUpdateProfile(userId, dto);
    }

    public ProfileDto updateProfile(Long userId, ProfileDto dto) {
        return saveOrUpdateProfile(userId, dto);
    }

    private ProfileDto saveOrUpdateProfile(Long userId, ProfileDto dto) {
        log.info("Saving/Updating profile for userId: {}, role={}, domain={}", userId, dto.getTargetRole(), dto.getDomain());
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
        log.info("Successfully saved profile id {} for userId {}", saved.getId(), userId);

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
