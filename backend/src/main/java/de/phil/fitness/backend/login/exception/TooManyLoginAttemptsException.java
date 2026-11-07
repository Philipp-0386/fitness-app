package de.phil.fitness.backend.login.exception;

import org.springframework.http.HttpStatus;

import de.phil.fitness.backend.common.ApiException;

public class TooManyLoginAttemptsException extends ApiException {
    public TooManyLoginAttemptsException(String message) {
        super(HttpStatus.TOO_MANY_REQUESTS, "TOO_MANY_LOGIN_ATTEMPTS",
                "Too many failed login attempts. Please try again later.", message);
    }
}
