package com.placementor.backend.dto;

public class UserResponseDto {
    private Long id;
    private String email;

    public UserResponseDto() {}

    public UserResponseDto(Long id, String email) {
        this.id = id;
        this.email = email;
    }

    public static UserResponseDtoBuilder builder() {
        return new UserResponseDtoBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public static class UserResponseDtoBuilder {
        private Long id;
        private String email;

        public UserResponseDtoBuilder id(Long id) { this.id = id; return this; }
        public UserResponseDtoBuilder email(String email) { this.email = email; return this; }
        public UserResponseDto build() { return new UserResponseDto(id, email); }
    }
}
