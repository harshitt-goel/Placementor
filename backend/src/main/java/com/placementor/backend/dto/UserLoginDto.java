package com.placementor.backend.dto;

public class UserLoginDto {
    private String email;
    private String username;
    private String password;

    public UserLoginDto() {}

    public UserLoginDto(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public static UserLoginDtoBuilder builder() {
        return new UserLoginDtoBuilder();
    }

    public String getEffectiveEmail() {
        if (email != null && !email.isBlank()) {
            return email;
        }
        return username;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public static class UserLoginDtoBuilder {
        private String email;
        private String username;
        private String password;

        public UserLoginDtoBuilder email(String email) { this.email = email; return this; }
        public UserLoginDtoBuilder username(String username) { this.username = username; return this; }
        public UserLoginDtoBuilder password(String password) { this.password = password; return this; }
        public UserLoginDto build() { return new UserLoginDto(email, username, password); }
    }
}
