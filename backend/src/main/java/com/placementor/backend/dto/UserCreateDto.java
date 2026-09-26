package com.placementor.backend.dto;

public class UserCreateDto {
    private String email;
    private String password;

    public UserCreateDto() {}

    public UserCreateDto(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public static UserCreateDtoBuilder builder() {
        return new UserCreateDtoBuilder();
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public static class UserCreateDtoBuilder {
        private String email;
        private String password;

        public UserCreateDtoBuilder email(String email) { this.email = email; return this; }
        public UserCreateDtoBuilder password(String password) { this.password = password; return this; }
        public UserCreateDto build() { return new UserCreateDto(email, password); }
    }
}
