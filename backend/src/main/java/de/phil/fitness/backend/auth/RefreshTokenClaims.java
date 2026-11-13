package de.phil.fitness.backend.auth;

import java.time.Instant;
import java.util.UUID;

/**
 * The claims of a verified refresh token.
 * @param userId the user the token was issued to
 * @param sessionId the session the token belongs to, shared by every refresh token since the login
 * @param issuedAt when the token was issued, with second precision
 */
public record RefreshTokenClaims(Long userId, UUID sessionId, Instant issuedAt) {}
