package de.phil.fitness.backend.exercise.exception;

/**
 * Thrown when given id references non-existent exercise, or requesting caller is not permitted to access it (not the owner).
 *
 * <p>Covers the soft deleted case as well. The causes are intentionally not distinguished, so that no response reveals
 * the existence of an exercise the caller may not read.
 */
public class ExerciseNotFoundException extends RuntimeException {
    public ExerciseNotFoundException(String msg) {
        super(msg);
    }
}
