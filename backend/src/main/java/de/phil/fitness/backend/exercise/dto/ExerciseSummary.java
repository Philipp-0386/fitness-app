package de.phil.fitness.backend.exercise.dto;

import de.phil.fitness.backend.exercise.model.TrackingType;

//Used by the "routine" slice as a compact view of exercises within a routine.
public record ExerciseSummary(
        Long id,
        String name,
        TrackingType trackingType,
        boolean deleted
) {}
