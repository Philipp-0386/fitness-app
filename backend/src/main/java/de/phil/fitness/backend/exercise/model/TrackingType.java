package de.phil.fitness.backend.exercise.model;

/**
 * Which values a logged set of an exercise holds, mirroring the check constraint on {@code exercise.tracking_type}
 */
public enum TrackingType {
    WEIGHT_REPS,
    BODYWEIGHT_REPS,
    ASSISTED_REPS,
    DURATION,
    WEIGHT_DISTANCE,
    DISTANCE_DURATION
}
