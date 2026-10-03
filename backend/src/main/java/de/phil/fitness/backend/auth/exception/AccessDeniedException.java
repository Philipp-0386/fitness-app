package de.phil.fitness.backend.auth.exception;

import org.springframework.http.HttpStatus;

import de.phil.fitness.backend.common.ApiException;

/**
 * Thrown when an authenticated user requests a resource owned by someone else.
 */
public class AccessDeniedException extends ApiException {

    public AccessDeniedException(String message) {
        super(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "You do not have access to this resource", message);
    }
}
