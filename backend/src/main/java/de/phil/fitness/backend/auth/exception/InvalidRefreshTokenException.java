package de.phil.fitness.backend.auth.exception;

import org.springframework.http.HttpStatus;

import de.phil.fitness.backend.common.ApiException;

public class InvalidRefreshTokenException extends ApiException {
    public InvalidRefreshTokenException(String msg) {
        super(HttpStatus.UNAUTHORIZED, "UNAUTHENTICATED", "Authentication is required to access this resource", msg);
    }
}
