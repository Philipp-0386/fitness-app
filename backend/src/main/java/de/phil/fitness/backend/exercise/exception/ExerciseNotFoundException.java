package de.phil.fitness.backend.exercise.exception;

public class ExerciseNotFoundException extends RuntimeException {
    public ExerciseNotFoundException(String msg) {
        super(msg);
    }
}
