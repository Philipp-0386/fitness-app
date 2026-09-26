package de.phil.fitness.backend.signup.exception;

public class SignUpDisabledException extends RuntimeException {
    public SignUpDisabledException(String message) {
        super(message);
    }
}
