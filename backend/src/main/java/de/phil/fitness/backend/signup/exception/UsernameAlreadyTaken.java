package de.phil.fitness.backend.signup.exception;

public class UsernameAlreadyTaken extends RuntimeException{
    public UsernameAlreadyTaken(String message) {
        super(message);
    }
}
