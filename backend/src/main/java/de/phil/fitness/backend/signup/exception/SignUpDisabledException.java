package de.phil.fitness.backend.signup.exception;

import org.springframework.http.HttpStatus;

import de.phil.fitness.backend.common.ApiException;

public class SignUpDisabledException extends ApiException {
    public SignUpDisabledException(String message) {
        super(HttpStatus.FORBIDDEN, "SIGNUP_DISABLED", "SignUps are currently disabled", message);
    }
}
