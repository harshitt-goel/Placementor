package com.placementor.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AuthResponseDto {
    private UserResponseDto user;

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType = "bearer";

    public AuthResponseDto() {}

    public AuthResponseDto(UserResponseDto user, String accessToken, String tokenType) {
        this.user = user;
        this.accessToken = accessToken;
        this.tokenType = tokenType != null ? tokenType : "bearer";
    }

    public static AuthResponseDtoBuilder builder() {
        return new AuthResponseDtoBuilder();
    }

    public UserResponseDto getUser() { return user; }
    public void setUser(UserResponseDto user) { this.user = user; }

    @JsonProperty("access_token")
    public String getAccessToken() { return accessToken; }
    @JsonProperty("access_token")
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    @JsonProperty("token_type")
    public String getTokenType() { return tokenType; }
    @JsonProperty("token_type")
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public static class AuthResponseDtoBuilder {
        private UserResponseDto user;
        private String accessToken;
        private String tokenType = "bearer";

        public AuthResponseDtoBuilder user(UserResponseDto user) { this.user = user; return this; }
        public AuthResponseDtoBuilder accessToken(String accessToken) { this.accessToken = accessToken; return this; }
        public AuthResponseDtoBuilder tokenType(String tokenType) { this.tokenType = tokenType; return this; }
        public AuthResponseDto build() { return new AuthResponseDto(user, accessToken, tokenType); }
    }
}
