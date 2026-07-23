package com.example.SpringDataJpaDemo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Creates and verifies JWT tokens (JJWT library).
 *
 * @Service — Spring bean
 * @Value("${jwt.secret}") — reads secret from application.properties
 *
 * Token payload we use:
 *  - subject = user email (username)
 *  - iat / exp = issued-at and expiry (15 minutes)
 */
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    /** HMAC signing key derived from the configured secret string. */
    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /** Called after successful login — returns compact JWT string. */
    public String generateJwtToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                .signWith(getKey())
                .compact();
    }

    /**
     * Parses + verifies signature/expiry.
     * Throws if token is tampered or expired — JwtAuthFilter catches that.
     * Claims — map-like object; getSubject() returns the email we stored.
     */
    public Claims parseToken(String token) {
        return Jwts.parser().verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
