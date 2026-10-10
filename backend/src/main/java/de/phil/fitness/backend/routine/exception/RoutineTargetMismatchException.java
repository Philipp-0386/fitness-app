package de.phil.fitness.backend.routine.exception;

import de.phil.fitness.backend.common.ApiException;
import org.springframework.http.HttpStatus;

import java.util.Map;

/**
 * Thrown, when a routine exercise sets a target that the tracking type of its exercise does not use.
 */
public class RoutineTargetMismatchException extends ApiException {
    public RoutineTargetMismatchException(String message, Map<String, String> fieldErrors) {
        super(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", "Request body contains invalid fields", message, fieldErrors);
    }
}
