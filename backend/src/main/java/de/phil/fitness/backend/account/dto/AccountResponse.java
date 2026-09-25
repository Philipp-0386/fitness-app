package de.phil.fitness.backend.account.dto;

import java.time.Instant;

/**
 * Account of the authenticated user as returned to the client.
 *
 * @param username Login name
 * @param email Email address
 * @param createdAt Time of sign-up
 */
public record AccountResponse(
        String username,
        String email,
        Instant createdAt
) {}
