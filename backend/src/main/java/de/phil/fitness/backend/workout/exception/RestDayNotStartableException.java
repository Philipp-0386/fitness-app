package de.phil.fitness.backend.workout.exception;

import de.phil.fitness.backend.common.ApiException;
import org.springframework.http.HttpStatus;

/**
 * Thrown, when a workout is started from a program day that is a rest day.
 */
public class RestDayNotStartableException extends ApiException {
    public RestDayNotStartableException(String message) {
        super(HttpStatus.BAD_REQUEST, "REST_DAY_NOT_STARTABLE", "A rest day has no routine to start", message);
    }
}
