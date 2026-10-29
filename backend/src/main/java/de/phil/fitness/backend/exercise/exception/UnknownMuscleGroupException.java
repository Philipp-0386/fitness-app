package de.phil.fitness.backend.exercise.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;

import de.phil.fitness.backend.common.ApiException;

/**
 * Thrown when an exercise references a muscle group id that does not exist.
 */
public class UnknownMuscleGroupException extends ApiException {
    public UnknownMuscleGroupException(String msg) {
        super(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", "Request body contains invalid fields", msg,
                Map.of("muscles", "contains an unknown muscle group"));
    }
}
