package com.haojie.secret_santa.controller.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.haojie.secret_santa.model.payload.request.LoginRequest;
import com.haojie.secret_santa.model.payload.request.RegisterRequest;
import com.haojie.secret_santa.model.payload.request.RtRequest;
import com.haojie.secret_santa.model.payload.response.ApiResponse;
import com.haojie.secret_santa.model.payload.response.AuthResponse;
import com.haojie.secret_santa.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Register attempt for username: {}", request.getUsername());
        ApiResponse resp = authService.register(request);
        if (resp.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login attempt for username: {}", request.getUsername());
        AuthResponse resp = authService.authenticate(request);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RtRequest request) {
        log.info("Refresh token request");
        AuthResponse resp = authService.refresh(request);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(@Valid @RequestBody RtRequest request) {
        log.info("Logout request");
        ApiResponse resp = authService.logout(request);
        return ResponseEntity.ok(resp);
    }

}
