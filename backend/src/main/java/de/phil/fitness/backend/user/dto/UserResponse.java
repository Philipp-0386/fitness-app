package de.phil.fitness.backend.user.dto;

import java.time.Instant;

/**
 * The authenticated user as returned to the client.
 *
 * @param username Login name
 * @param email Email address
 * @param createdAt Time of sign-up
 */
public record UserResponse(
        String username,
        String email,
        Instant createdAt
) {}
