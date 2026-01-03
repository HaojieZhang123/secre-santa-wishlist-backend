package com.haojie.secret_santa.model.payload.response;

public class AuthResponse {

    private String jwtToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private String username;
    private String role;
    private String message;
    private Long expiresIn;

    public AuthResponse() {
    }

    // login complete info
    public AuthResponse(String jwtToken, String refreshToken, String username, String role, Long expiresIn) {
        this.jwtToken = jwtToken;
        this.refreshToken = refreshToken;
        this.username = username;
        this.role = role;
        this.expiresIn = expiresIn;
    }

    // refresh token info
    public AuthResponse(String token, String refreshToken, Long expiresIn) {
        this.jwtToken = token;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    @Override
    public String toString() {
        return "AuthResponse{" +
                "type='" + tokenType + '\'' +
                ", email='" + username + '\'' +
                ", role='" + role + '\'' +
                ", message='" + message + '\'' +
                ", expiresIn=" + expiresIn +
                ", hasAccessToken=" + (jwtToken != null) +
                ", hasRefreshToken=" + (refreshToken != null) +
                '}';
    }

}
