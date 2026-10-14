package de.phil.fitness.backend.exercise.exception;

import org.springframework.http.HttpStatus;

import de.phil.fitness.backend.common.ApiException;

/**
 * Thrown when the tracking type of an exercise is changed while it is referenced.
 */
public class TrackingTypeLockedException extends ApiException {
    public TrackingTypeLockedException(String msg) {
        super(HttpStatus.CONFLICT, "TRACKING_TYPE_LOCKED", "The tracking type can not be changed while the exercise is in use", msg);
    }
}
