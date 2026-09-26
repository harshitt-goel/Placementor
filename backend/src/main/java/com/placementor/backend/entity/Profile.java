package com.placementor.backend.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "profiles")
public class Profile implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "target_role")
    private String targetRole;

    private String domain;

    @Column(name = "current_level")
    private String currentLevel;

    @Column(name = "github_url")
    private String githubUrl;

    @Column(name = "leetcode_url")
    private String leetcodeUrl;

    @Column(name = "codeforces_url")
    private String codeforcesUrl;

    @Column(name = "target_company")
    private String targetCompany;

    public Profile() {}

    public Profile(Long id, Long userId, String targetRole, String domain, String currentLevel, String githubUrl, String leetcodeUrl, String codeforcesUrl, String targetCompany) {
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

    public static ProfileBuilder builder() {
        return new ProfileBuilder();
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

    public static class ProfileBuilder {
        private Long id;
        private Long userId;
        private String targetRole;
        private String domain;
        private String currentLevel;
        private String githubUrl;
        private String leetcodeUrl;
        private String codeforcesUrl;
        private String targetCompany;

        public ProfileBuilder id(Long id) { this.id = id; return this; }
        public ProfileBuilder userId(Long userId) { this.userId = userId; return this; }
        public ProfileBuilder targetRole(String targetRole) { this.targetRole = targetRole; return this; }
        public ProfileBuilder domain(String domain) { this.domain = domain; return this; }
        public ProfileBuilder currentLevel(String currentLevel) { this.currentLevel = currentLevel; return this; }
        public ProfileBuilder githubUrl(String githubUrl) { this.githubUrl = githubUrl; return this; }
        public ProfileBuilder leetcodeUrl(String leetcodeUrl) { this.leetcodeUrl = leetcodeUrl; return this; }
        public ProfileBuilder codeforcesUrl(String codeforcesUrl) { this.codeforcesUrl = codeforcesUrl; return this; }
        public ProfileBuilder targetCompany(String targetCompany) { this.targetCompany = targetCompany; return this; }

        public Profile build() {
            return new Profile(id, userId, targetRole, domain, currentLevel, githubUrl, leetcodeUrl, codeforcesUrl, targetCompany);
        }
    }
}
