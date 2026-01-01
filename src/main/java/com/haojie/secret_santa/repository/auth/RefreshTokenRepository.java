package com.haojie.secret_santa.repository.auth;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.haojie.secret_santa.model.auth.RefreshToken;
import com.haojie.secret_santa.model.auth.User;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // find by token by UUID string
    @Query("SELECT rt FROM RefreshToken rt JOIN FETCH rt.user WHERE rt.token = :token")
    Optional<RefreshToken> findByToken(@Param("token") String token);

    // find all active tokens by user
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.user = :user " +
            "AND rt.revoked = false AND rt.expiryDate > :now")
    List<RefreshToken> findActiveTokensByUser(@Param("user") User user, @Param("now") Instant now);

    // find oldest active token by user
    @Query("""
            SELECT rt FROM RefreshToken rt
            WHERE rt.user = :user AND rt.revoked = false AND rt.expiryDate > :now
            ORDER BY rt.createdAt ASC
            """)
    RefreshToken findOldestActiveTokenByUser(User user, Instant now);

    // count all active tokens by user
    @Query("SELECT COUNT(rt) FROM RefreshToken rt WHERE rt.user = :user " +
            "AND rt.revoked = false AND rt.expiryDate > :now")
    long countActiveTokensByUser(@Param("user") User user, @Param("now") Instant now);

    // revoke all tokens by user by setting revoked = true
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.user = :user")
    int revokeAllUserTokens(@Param("user") User user);

    // delete all tokens by user
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.user = :user")
    int deleteByUser(@Param("user") User user);

    // delete all expired token from DB
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiryDate < :now")
    int deleteAllExpiredTokens(@Param("now") Instant now);

    // delete all revoked tokens older than given instant
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.revoked = true AND rt.createdAt < :olderThan")
    int deleteRevokedTokensOlderThan(@Param("olderThan") Instant olderThan);

    // find suspicious tokens: revoked tokens that were used after revocation
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.revoked = true AND rt.expiryDate > :now")
    List<RefreshToken> findSuspiciousTokens(@Param("now") Instant now);

}