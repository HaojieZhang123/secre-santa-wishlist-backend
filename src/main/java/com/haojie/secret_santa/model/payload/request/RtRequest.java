package com.haojie.secret_santa.model.payload.request;

import jakarta.validation.constraints.NotBlank;

public class RtRequest {

    @NotBlank(message = "Refresh token must not be blank")
    private String refreshToken;

    public RtRequest() {
    }

    public RtRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    public String toString() {
        String masked = refreshToken != null && refreshToken.length() > 8
                ? refreshToken.substring(0, 4) + "****" + refreshToken.substring(refreshToken.length() - 4)
                : "****";

        return "RtRequest{" +
                "refreshToken='" + masked + '\'' +
                '}';
    }

}
