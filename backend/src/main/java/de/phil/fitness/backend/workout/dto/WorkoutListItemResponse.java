package de.phil.fitness.backend.workout.dto;

import java.time.Instant;
import java.util.List;

/**
 * Used during all workouts call for user's workouts. Shortened view without sets.
 */
public record WorkoutListItemResponse(
        Long id,
        String name,
        Instant startedAt,
        Instant endedAt,
        int setCount,
        List<String> exerciseNames
) {}
