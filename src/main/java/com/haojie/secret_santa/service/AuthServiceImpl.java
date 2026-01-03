package com.haojie.secret_santa.service;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.haojie.secret_santa.model.auth.ERole;
import com.haojie.secret_santa.model.auth.RefreshToken;
import com.haojie.secret_santa.model.auth.Role;
import com.haojie.secret_santa.model.auth.User;
import com.haojie.secret_santa.model.exception.InvalidTokenException;
import com.haojie.secret_santa.model.payload.request.LoginRequest;
import com.haojie.secret_santa.model.payload.request.RegisterRequest;
import com.haojie.secret_santa.model.payload.request.RtRequest;
import com.haojie.secret_santa.model.payload.response.ApiResponse;
import com.haojie.secret_santa.model.payload.response.AuthResponse;
import com.haojie.secret_santa.repository.auth.RoleRepository;
import com.haojie.secret_santa.repository.auth.UserRepository;
import com.haojie.secret_santa.security.UserDetailsImpl;
import com.haojie.secret_santa.security.jwt.JwtUtils;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            JwtUtils jwtUtils,
            RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public ApiResponse register(RegisterRequest request) {
        // validate uniqueness
        if (userRepository.existsByUsername(request.getUsername())) {
            return new ApiResponse(false, "Username already taken", null);
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // assign role(s)
        Set<Role> roles = new HashSet<>();
        String requestedRole = request.getRole();
        Role role = null;

        if (requestedRole == null || requestedRole.isBlank()) {
            role = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Default role ROLE_USER not found"));
        } else {
            // Normalize provided role value
            String normalized = requestedRole.trim().toUpperCase();
            if (!normalized.startsWith("ROLE_")) {
                normalized = "ROLE_" + normalized;
            }
            ERole eRole;
            try {
                eRole = ERole.valueOf(normalized);
            } catch (IllegalArgumentException ex) {
                return new ApiResponse(false, "Invalid role provided", null);
            }
            role = roleRepository.findByName(eRole)
                    .orElseThrow(() -> new RuntimeException("Role not found: " + eRole));
        }

        roles.add(role);
        user.setRoles(roles);

        User saved = userRepository.save(user);
        logger.info("New user registered: {}", saved.getUsername());

        return new ApiResponse(true, "User registered successfully", null);
    }

    @Override
    public AuthResponse authenticate(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtils.generateJwtToken(userDetails);

        // load User entity
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found after authentication"));

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        String role = user.getRoles().stream().findFirst().map(r -> r.getName().name()).orElse("ROLE_USER");

        long expiresInMillis = 0L;
        try {
            expiresInMillis = jwtUtils.extractExpiration(jwt).getTime() - System.currentTimeMillis();
        } catch (Exception e) {
            // ignore - leave 0
        }

        AuthResponse resp = new AuthResponse(jwt, refreshToken.getToken(), user.getUsername(), role, expiresInMillis);

        return resp;
    }

    @Override
    public AuthResponse refresh(RtRequest request) {
        String rt = request.getRefreshToken();

        RefreshToken token = refreshTokenService.verifyRefreshToken(rt);
        User user = token.getUser();

        // build UserDetails for jwt generation
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        String jwt = jwtUtils.generateJwtToken(userDetails);

        // rotate refresh token for safety
        RefreshToken newRefresh = refreshTokenService.rotateRefreshToken(rt, null, null);

        long expiresInMillis = 0L;
        try {
            expiresInMillis = jwtUtils.extractExpiration(jwt).getTime() - System.currentTimeMillis();
        } catch (Exception e) {
        }

        return new AuthResponse(jwt, newRefresh.getToken(), expiresInMillis);
    }

    @Override
    public ApiResponse logout(RtRequest request) {
        String rt = request.getRefreshToken();
        // find token and delete all user tokens
        RefreshToken token = null;
        try {
            token = refreshTokenService.verifyRefreshToken(rt);
        } catch (InvalidTokenException ex) {
            // if token invalid we still respond success to avoid token probing
            return new ApiResponse(true, "Logged out", null);
        }

        User user = token.getUser();
        int deleted = refreshTokenService.deleteAllUserTokens(user);

        return new ApiResponse(true, "Logged out; tokens removed: " + deleted, null);
    }

    @Override
    public void revokeRefreshToken(String tokenValue) {
        refreshTokenService.revokeToken(tokenValue);
    }

}