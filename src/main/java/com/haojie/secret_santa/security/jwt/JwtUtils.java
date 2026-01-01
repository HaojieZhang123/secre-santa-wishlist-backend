package com.haojie.secret_santa.security.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {

    // get jwtSecretKey and jwtExpirationMs from application.properties
    @Value("${haojie.secretSanta.jwtSecret}")
    private String jwtSecretKey;

    @Value("${haojie.secretSanta.jwtExpirationMs}")
    private long jwtExpirationMs;

    private SecretKey jwtSecretKey() {
        // hmacShaKeyFor requires byte[]
        // convert jwtSecretKey to byte[]
        // StandardCharsets.UTF_8 to avoid platform dependent issues on systems with
        // different default charsets
        byte[] keyBytes = jwtSecretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // generate token
    public String generateJwtToken(UserDetails userDetails) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpirationMs);
        String username = userDetails.getUsername();
        // add role claims. Get Authorities from UserDetails, get authority string from
        // each GrantedAuthority, collect to array
        String[] roles = userDetails.getAuthorities().stream()
                .map(role -> role.getAuthority())
                .toArray(String[]::new);

        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(jwtSecretKey())
                .compact();
    }

    // extract all claims
    // Claims is a Map-like object that contains all the claims
    public Claims extractAllClaims(String token) {
        // parse the token
        return Jwts.parser() // create a parser
                .verifyWith(jwtSecretKey()) // verify the signature
                .build() // build the parser
                .parseSignedClaims(token) // split the token, decode base64 header and payload, verify signature
                .getPayload(); // extract and returns the payload as Claims
    }

    // extract claim by resolver
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // extract username
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // extract expiration
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // extract roles
    public String[] extractRoles(String token) {
        return extractClaim(token, claims -> claims.get("roles", String[].class));
        // get claim "roles" and convert to String[]
    }

    // check if token is expired
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // validate token with user details
    public boolean validateJwtToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

}
