package com.haojie.secret_santa.service;

import com.haojie.secret_santa.model.payload.request.LoginRequest;
import com.haojie.secret_santa.model.payload.request.RegisterRequest;
import com.haojie.secret_santa.model.payload.request.RtRequest;
import com.haojie.secret_santa.model.payload.response.ApiResponse;
import com.haojie.secret_santa.model.payload.response.AuthResponse;

/**
 * Service that handles authentication flows: register, authenticate (login),
 * refresh tokens and logout.
 */
public interface AuthService {

    /**
     * Register a new user with the provided request. Returns ApiResponse with
     * success or failure.
     */
    ApiResponse register(RegisterRequest request);

    /**
     * Authenticate credentials and return access + refresh tokens.
     */
    AuthResponse authenticate(LoginRequest request);

    /**
     * Refresh an access token using a provided refresh token.
     */
    AuthResponse refresh(RtRequest request);

    /**
     * Logout a user by revoking/deleting refresh tokens related to provided refresh
     * token.
     */
    ApiResponse logout(RtRequest request);

    /**
     * Revoke a refresh token by its token string.
     */
    void revokeRefreshToken(String tokenValue);

}
