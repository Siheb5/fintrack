package com.fintrack.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

/**
 * Creates and validates JWT tokens.
 *
 * On login we call generateToken(userId) and hand the token to the client.
 * On every later request, the filter calls extractUserId(token) to find out
 * who is calling — but only after the signature is verified, so a forged or
 * tampered token is rejected automatically.
 */
@Service
public class JwtService {

    private final SecretKey key;
    private final long expirationMs;

    public JwtService(JwtProperties props) {
        // Turn the configured secret string into a cryptographic signing key.
        this.key = Keys.hmacShaKeyFor(props.secret().getBytes(StandardCharsets.UTF_8));
        this.expirationMs = props.expirationMs();
    }

    /** Build a signed token whose "subject" is the user's id. */
    public String generateToken(Long userId) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(userId))          // who the token belongs to
                .issuedAt(Date.from(now))                 // when issued
                .expiration(Date.from(now.plusMillis(expirationMs)))  // when it expires
                .signWith(key)                            // sign with our secret
                .compact();                               // produce the final string
    }

    /**
     * Verify the token's signature + expiry and return the user id inside it.
     * Throws if the token is invalid, tampered, or expired.
     */
    public Long extractUserId(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)        // checks the signature using our secret
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return Long.valueOf(claims.getSubject());
    }
}
