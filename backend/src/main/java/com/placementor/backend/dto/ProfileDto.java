package com.placementor.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProfileDto {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("target_role")
    private String targetRole;

    @JsonProperty("domain")
    private String domain;

    @JsonProperty("current_level")
    private String currentLevel;

    @JsonProperty("github_url")
    private String githubUrl;

    @JsonProperty("leetcode_url")
    private String leetcodeUrl;

    @JsonProperty("codeforces_url")
    private String codeforcesUrl;

    @JsonProperty("target_company")
    private String targetCompany;

    public ProfileDto() {}

    public ProfileDto(Long id, Long userId, String targetRole, String domain, String currentLevel, String githubUrl, String leetcodeUrl, String codeforcesUrl, String targetCompany) {
        this.id = id;
        this.userId = userId;
        this.targetRole = targetRole;
        this.domain = domain;
        this.currentLevel = currentLevel;
        this.githubUrl = githubUrl;
        this.leetcodeUrl = leetcodeUrl;
        this.codeforcesUrl = codeforcesUrl;
        this.targetCompany = targetCompany;
    }

    public static ProfileDtoBuilder builder() {
        return new ProfileDtoBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getTargetRole() { return targetRole; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }

    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }

    public String getCurrentLevel() { return currentLevel; }
    public void setCurrentLevel(String currentLevel) { this.currentLevel = currentLevel; }

    public String getGithubUrl() { return githubUrl; }
    public void setGithubUrl(String githubUrl) { this.githubUrl = githubUrl; }

    public String getLeetcodeUrl() { return leetcodeUrl; }
    public void setLeetcodeUrl(String leetcodeUrl) { this.leetcodeUrl = leetcodeUrl; }

    public String getCodeforcesUrl() { return codeforcesUrl; }
    public void setCodeforcesUrl(String codeforcesUrl) { this.codeforcesUrl = codeforcesUrl; }

    public String getTargetCompany() { return targetCompany; }
    public void setTargetCompany(String targetCompany) { this.targetCompany = targetCompany; }

    public static class ProfileDtoBuilder {
        private Long id;
        private Long userId;
        private String targetRole;
        private String domain;
        private String currentLevel;
        private String githubUrl;
        private String leetcodeUrl;
        private String codeforcesUrl;
        private String targetCompany;

        public ProfileDtoBuilder id(Long id) { this.id = id; return this; }
        public ProfileDtoBuilder userId(Long userId) { this.userId = userId; return this; }
        public ProfileDtoBuilder targetRole(String targetRole) { this.targetRole = targetRole; return this; }
        public ProfileDtoBuilder domain(String domain) { this.domain = domain; return this; }
        public ProfileDtoBuilder currentLevel(String currentLevel) { this.currentLevel = currentLevel; return this; }
        public ProfileDtoBuilder githubUrl(String githubUrl) { this.githubUrl = githubUrl; return this; }
        public ProfileDtoBuilder leetcodeUrl(String leetcodeUrl) { this.leetcodeUrl = leetcodeUrl; return this; }
        public ProfileDtoBuilder codeforcesUrl(String codeforcesUrl) { this.codeforcesUrl = codeforcesUrl; return this; }
        public ProfileDtoBuilder targetCompany(String targetCompany) { this.targetCompany = targetCompany; return this; }

        public ProfileDto build() {
            return new ProfileDto(id, userId, targetRole, domain, currentLevel, githubUrl, leetcodeUrl, codeforcesUrl, targetCompany);
        }
    }
}
