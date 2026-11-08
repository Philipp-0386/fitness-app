package de.phil.fitness.backend.workout.model;

/**
 * Kind of a logged set, mirroring the check constraint on {@code workout_set.set_type}
 */
public enum SetType {
    WARMUP,
    WORKING,
    DROP
}
