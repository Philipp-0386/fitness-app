package de.phil.fitness.backend.workout.repository;

public interface WorkoutEntryCounts {
    Long getWorkoutId();
    Long getExerciseId();
    Long getSetCount();
}
