package de.phil.fitness.backend.common;

import java.time.LocalDateTime;

/**
 * Custom error response class for exception handling.
 * @param message   Message with context information
 * @param status    Contains http code
 * @param timestamp Documents time of creation
 */
public record ErrorResponse(
        String message,
        int status,
        LocalDateTime timestamp
) {
    public ErrorResponse(String message, int status) {
        this(message, status, LocalDateTime.now());
    }
}
