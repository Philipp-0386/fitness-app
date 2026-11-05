package de.phil.fitness.backend.workout.exception;

import de.phil.fitness.backend.common.ApiException;
import org.springframework.http.HttpStatus;

import java.util.Map;

/**
 * Thrown, when the values of a set do not fit the tracking type of its exercise, or a set moves to another exercise.
 */
public class InvalidSetException extends ApiException {
    public InvalidSetException(String message, Map<String, String> fieldErrors) {
        super(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", "Request body contains invalid fields", message, fieldErrors);
    }
}
