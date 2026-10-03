package de.phil.fitness.backend.user.exception;

import org.springframework.http.HttpStatus;

import de.phil.fitness.backend.common.ApiException;

/**
 * Thrown when no user exists for an id, typically the subject of a still valid access token whose
 * account was deleted.
 * Answers like a missing token, because the session is no longer usable
 */
public class UserNotFoundException extends ApiException {
    public UserNotFoundException(String msg) {
        super(HttpStatus.UNAUTHORIZED, "UNAUTHENTICATED", "Authentication is required to access this resource", msg);
    }
}
