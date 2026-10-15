package de.phil.fitness.backend.exercise.exception;

import org.springframework.http.HttpStatus;

import de.phil.fitness.backend.common.ApiException;

/**
 * Thrown when a user tries to change an exercise of the global catalog.
 * 403 instead of 404, because global exercises are visible to everyone and their existence is no secret.
 */
public class GlobalExerciseNotEditableException extends ApiException {
    public GlobalExerciseNotEditableException(String msg) {
        super(HttpStatus.FORBIDDEN, "GLOBAL_EXERCISE_NOT_EDITABLE", "Exercises of the global catalog can not be changed", msg);
    }
}
