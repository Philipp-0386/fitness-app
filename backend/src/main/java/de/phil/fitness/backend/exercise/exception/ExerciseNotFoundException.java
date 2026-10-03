package de.phil.fitness.backend.exercise.exception;

import org.springframework.http.HttpStatus;

import de.phil.fitness.backend.common.ApiException;

/**
 * Thrown when given id references non-existent exercise, or requesting caller is not permitted to access it (not the owner).
 * Covers the soft deleted case as well. The causes are intentionally not distinguishable. 403 for the unowned case would confirm that the id exists.
 */
public class ExerciseNotFoundException extends ApiException {
    public ExerciseNotFoundException(String msg) {
        super(HttpStatus.NOT_FOUND, "EXERCISE_NOT_FOUND", "No exercise with this id is available", msg);
    }
}
