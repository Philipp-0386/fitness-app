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
    DISTANCE_DURATION;

    public boolean holdsReps() {
        return this == WEIGHT_REPS || this == BODYWEIGHT_REPS || this == ASSISTED_REPS;
    }

    public boolean holdsDuration() {
        return this == DURATION || this == DISTANCE_DURATION;
    }

    public boolean holdsDistance() {
        return this == WEIGHT_DISTANCE || this == DISTANCE_DURATION;
    }
}
