package com.haojie.secret_santa.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.haojie.secret_santa.model.auth.RefreshToken;
import com.haojie.secret_santa.model.auth.User;
import com.haojie.secret_santa.model.exception.InvalidTokenException;
import com.haojie.secret_santa.repository.auth.RefreshTokenRepository;

@Service
@Transactional
public class RefreshTokenService {

    private static final Logger logger = LoggerFactory.getLogger(RefreshTokenService.class);

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${haojie.jwtdemo.refreshExpirationDays:7}")
    private int refreshTokenDurationDays;

    @Value("${refresh-token.max-per-user:5}")
    private int maxTokensPerUser;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken createRefreshToken(User user) {
        return createRefreshToken(user, null, null);
    }

    public RefreshToken createRefreshToken(User user, String userAgent, String ipAddress) {
        // Verify if user has exceeded max active tokens
        long activeTokensCount = refreshTokenRepository.countActiveTokensByUser(user, Instant.now());

        if (activeTokensCount >= maxTokensPerUser) {
            logger.warn("User {} has reached the limit of {} active tokens.", user.getUsername(), maxTokensPerUser);

            // Find and delete the oldest active token
            RefreshToken oldestToken = refreshTokenRepository.findOldestActiveTokenByUser(user, Instant.now());
            if (oldestToken != null) {
                logger.info("Deleting oldest token: {}", oldestToken.getToken());
                refreshTokenRepository.delete(oldestToken);
            }
        }

        // Generate new token
        String tokenValue = UUID.randomUUID().toString();
        Instant expiryDate = Instant.now().plus(refreshTokenDurationDays, ChronoUnit.DAYS);

        RefreshToken refreshToken = new RefreshToken(tokenValue, user, expiryDate);
        refreshToken.setUserAgent(userAgent);
        refreshToken.setIpAddress(ipAddress);
        refreshToken.setCreatedAt(Instant.now());

        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);

        logger.info("New refresh token created for user {} (expiry: {})",
                user.getUsername(), expiryDate);

        return savedToken;
    }

    public RefreshToken verifyRefreshToken(String tokenValue) {
        RefreshToken token = refreshTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> {
                    logger.warn("Token does not exist.");
                    return new InvalidTokenException("Refresh token not found or not valid");
                });

        // SECURITY CHECK: Token Reuse Detection
        if (token.isRevoked()) {
            logger.error("SECURITY ALERT: You are attempting to reuse a revoked token. " +
                    "Token: {} | User: {}",
                    tokenValue, token.getUser().getUsername());

            // Revoke ALL token of the user
            int revokedCount = refreshTokenRepository.revokeAllUserTokens(token.getUser());

            logger.error("All {} user's token(s) have been revoked for security reasons. " +
                    "User must re-authenticate. User: {}",
                    revokedCount, token.getUser().getUsername());

            throw new InvalidTokenException(
                    "Attempted use of revoked token. For security reasons, all sessions have been revoked. " +
                            "Please re-authenticate.");
        }

        // Check expiration
        if (token.isExpired()) {
            logger.info("Refresh token expired for user {}", token.getUser().getUsername());
            throw new InvalidTokenException("Refresh token scaduto. Effettua nuovamente il login.");
        }

        logger.info("Refresh token verified for user {}",
                token.getUser().getUsername());

        return token;
    }

    public RefreshToken rotateRefreshToken(String oldTokenValue, String userAgent, String ipAddress) {
        logger.info("Rotating refresh token");

        // 1. Verify old token (include all security checks)
        RefreshToken oldToken = verifyRefreshToken(oldTokenValue);
        User user = oldToken.getUser();

        // 2. revoke old token
        oldToken.setRevoked(true);
        refreshTokenRepository.save(oldToken);
        logger.info("Old token revoked: {}", oldTokenValue);

        // 3. Genera nuovo token
        RefreshToken newToken = createRefreshToken(user, userAgent, ipAddress);
        logger.info("New token created: {} for user {}",
                newToken.getToken(), user.getUsername());

        return newToken;
    }

    public void revokeToken(String tokenValue) {
        RefreshToken token = refreshTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new InvalidTokenException("Token non trovato"));

        token.setRevoked(true);
        refreshTokenRepository.save(token);

        logger.info("Token revoked: {} (user: {})",
                tokenValue, token.getUser().getUsername());
    }

    public int revokeAllUserTokens(User user) {
        int count = refreshTokenRepository.revokeAllUserTokens(user);
        logger.warn("Revoked all {} tokens for user {}", count, user.getUsername());
        return count;
    }

    public int deleteAllUserTokens(User user) {
        int count = refreshTokenRepository.deleteByUser(user);
        logger.info("Deleted all {} tokens for user {}", count, user.getUsername());
        return count;
    }

    @Transactional(readOnly = true)
    public List<RefreshToken> getActiveUserTokens(User user) {
        return refreshTokenRepository.findActiveTokensByUser(user, Instant.now());
    }

    @Scheduled(cron = "${refresh-token.cleanup-schedule:0 0 3 * * ?}")
    @Transactional
    public void cleanupExpiredTokens() {
        logger.info("Cleaning up expired tokens...");

        Instant now = Instant.now();

        // 1. delete expired tokens
        int expiredCount = refreshTokenRepository.deleteAllExpiredTokens(now);
        logger.info("Deleted {} expired tokens", expiredCount);

        // 2. delete revoked tokens older than 30 days (others are kept for possible
        // forensics)
        Instant thirtyDaysAgo = now.minus(30, ChronoUnit.DAYS);
        int revokedCount = refreshTokenRepository.deleteRevokedTokensOlderThan(thirtyDaysAgo);
        logger.info("Deleted {} month oldrevoked tokens", revokedCount);

        logger.info("Cleanup completed: {} tokens removed ({} expired, {} revoked)",
                expiredCount + revokedCount, expiredCount, revokedCount);

    }

    @Scheduled(cron = "${refresh-token.cleanup-schedule:0 0 3 * * ?}")
    public void checkSuspiciousActivity() {
        List<RefreshToken> suspicious = refreshTokenRepository.findSuspiciousTokens(Instant.now());

        if (!suspicious.isEmpty()) {
            logger.warn("Found {} suspicious token(s) (revoked but still active)",
                    suspicious.size());

            for (RefreshToken token : suspicious) {
                logger.warn("Suspicious token: {} | User: {} | Created: {}",
                        token.getToken(),
                        token.getUser().getUsername(),
                        token.getCreatedAt());
            }
        }
    }

}
