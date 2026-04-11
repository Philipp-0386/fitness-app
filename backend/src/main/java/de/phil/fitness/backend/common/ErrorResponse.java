package de.phil.fitness.backend.common;

import java.time.Instant;
import java.util.Map;

/**
 * Custom error response class for exception handling.
 * @param code    Contains code evalutated by the frontend. Not http code!
 * @param message   Message with contextual information
 * @param path  Path of the request that caused the error
 * @param fieldErrors Contains field errors for validation exceptions (currently unused, but can be used in the future for more detailed error reporting)
 * @param timestamp Documents time of creation
 */
public record ErrorResponse(
        String code,
        String message,
        String path,
        Map<String, String> fieldErrors,
        Instant timestamp
) {
    public ErrorResponse(String code, String message, String path) {
        this(code, message, path, null, Instant.now());
    }

    public ErrorResponse(String code, String message, String path, Map<String, String> fieldErrors) {
        this(code, message, path, fieldErrors, Instant.now());
    }
}
