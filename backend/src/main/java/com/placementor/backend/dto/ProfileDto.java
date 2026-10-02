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

    @JsonProperty("id")
    public Long getId() { return id; }
    @JsonProperty("id")
    public void setId(Long id) { this.id = id; }

    @JsonProperty("user_id")
    public Long getUserId() { return userId; }
    @JsonProperty("user_id")
    public void setUserId(Long userId) { this.userId = userId; }

    @JsonProperty("target_role")
    public String getTargetRole() { return targetRole; }
    @JsonProperty("target_role")
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }

    @JsonProperty("domain")
    public String getDomain() { return domain; }
    @JsonProperty("domain")
    public void setDomain(String domain) { this.domain = domain; }

    @JsonProperty("current_level")
    public String getCurrentLevel() { return currentLevel; }
    @JsonProperty("current_level")
    public void setCurrentLevel(String currentLevel) { this.currentLevel = currentLevel; }

    @JsonProperty("github_url")
    public String getGithubUrl() { return githubUrl; }
    @JsonProperty("github_url")
    public void setGithubUrl(String githubUrl) { this.githubUrl = githubUrl; }

    @JsonProperty("leetcode_url")
    public String getLeetcodeUrl() { return leetcodeUrl; }
    @JsonProperty("leetcode_url")
    public void setLeetcodeUrl(String leetcodeUrl) { this.leetcodeUrl = leetcodeUrl; }

    @JsonProperty("codeforces_url")
    public String getCodeforcesUrl() { return codeforcesUrl; }
    @JsonProperty("codeforces_url")
    public void setCodeforcesUrl(String codeforcesUrl) { this.codeforcesUrl = codeforcesUrl; }

    @JsonProperty("target_company")
    public String getTargetCompany() { return targetCompany; }
    @JsonProperty("target_company")
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
