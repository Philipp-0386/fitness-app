package de.phil.fitness.backend.signup.exception;

public class EmailAlreadyExistsException extends RuntimeException{
    public EmailAlreadyExistsException(String msg) {
        super(msg);
    }
}
