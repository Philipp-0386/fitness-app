package de.phil.fitness.backend.workout.exception;

import de.phil.fitness.backend.common.ApiException;
import org.springframework.http.HttpStatus;

/**
 * Thrown, when a set refers to a workout exercise that does not belong to the workout.
 */
public class WorkoutExerciseNotFoundException extends ApiException {
    public WorkoutExerciseNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, "WORKOUT_EXERCISE_NOT_FOUND", "No exercise with this id belongs to the workout", message);
    }
}
