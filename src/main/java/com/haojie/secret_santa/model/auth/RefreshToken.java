package com.haojie.secret_santa.model.auth;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

// refresh token for JWT
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // token. simple string UUID
    @Column(nullable = false, unique = true, length = 36)
    private String token;

    // user associated with the token
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // expiry
    @Column(nullable = false)
    private Instant expiryDate;

    // revoked status. if true, token is invalid. default false
    @Column(nullable = false)
    private boolean revoked = false;

    // created at timestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    // userAgent
    @Column(length = 512)
    private String userAgent;

    // ip address
    @Column(length = 45) // IPv6 max length is 45 characters
    private String ipAddress;

    // constructors
    public RefreshToken() {
    }

    public RefreshToken(String token, User user, Instant expiryDate) {
        this.token = token;
        this.user = user;
        this.expiryDate = expiryDate;
    }

    // getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Instant getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Instant expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    // check if token is expired
    public boolean isExpired() {
        return Instant.now().isAfter(this.expiryDate);
    }

    // check if token is valid: not expired and not revoked
    public boolean isValid() {
        return !isExpired() && !isRevoked();
    }

    @Override
    public String toString() {
        return "RefreshToken" +
                "[id=" + id +
                ", token=" + token +
                ", userId=" + (user != null ? user.getId() : null) +
                ", expiryDate=" + expiryDate +
                ", revoked=" + revoked +
                ", createdAt=" + createdAt +
                "]";
    }
}
