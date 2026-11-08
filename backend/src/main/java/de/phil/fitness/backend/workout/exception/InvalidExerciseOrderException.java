package de.phil.fitness.backend.workout.exception;

import de.phil.fitness.backend.common.ApiException;
import org.springframework.http.HttpStatus;

import java.util.Map;

/**
 * Thrown, when a new order does not contain every exercise entry of the workout exactly once.
 */
public class InvalidExerciseOrderException extends ApiException {
    public InvalidExerciseOrderException(String message) {
        super(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", "Request body contains invalid fields", message,
                Map.of("entryIds", "must contain every exercise of the workout exactly once"));
    }
}
