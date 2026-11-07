package de.phil.fitness.backend.workout.model;

/**
 * Lifecycle of a workout, mirroring the check constraint on {@code workout.status}
 */
public enum WorkoutStatus {
    IN_PROGRESS,
    COMPLETED
}
