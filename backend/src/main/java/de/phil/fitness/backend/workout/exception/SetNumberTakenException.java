package de.phil.fitness.backend.workout.exception;

import de.phil.fitness.backend.common.ApiException;
import org.springframework.http.HttpStatus;

/**
 * Thrown, when a set takes a set number that another set of the same workout exercise already has.
 */
public class SetNumberTakenException extends ApiException {
    public SetNumberTakenException(String message) {
        super(HttpStatus.CONFLICT, "SET_NUMBER_TAKEN", "Another set of this exercise already has this number", message);
    }
}
