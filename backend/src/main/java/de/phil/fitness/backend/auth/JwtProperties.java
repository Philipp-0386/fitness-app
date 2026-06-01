package de.phil.fitness.backend.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *
 * @param secret
 * @param accessTokenExpirationMinutes
 * @param refreshTokenExpirationDays
 */
@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(
        String secret,
        long accessTokenExpirationMinutes,
        long refreshTokenExpirationDays
) {}
