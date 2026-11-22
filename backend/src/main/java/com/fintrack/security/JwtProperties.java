package com.fintrack.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Binds the "fintrack.jwt.*" settings from application.yml into a typed object.
 *   secret       -> the key used to sign/verify tokens (must stay secret!)
 *   expirationMs -> how long a token stays valid, in milliseconds
 */
@ConfigurationProperties(prefix = "fintrack.jwt")
public record JwtProperties(String secret, long expirationMs) {
}
