package de.phil.fitness.backend.workout.dto;

import de.phil.fitness.backend.workout.model.WorkoutStatus;

import java.time.Instant;
import java.util.List;

public record WorkoutResponse(
        Long id,
        String name,
        Long routineId,
        Long programDayId,
        WorkoutStatus status,
        Instant startedAt,
        Instant endedAt,
        List<WorkoutExerciseResponse> exercises
) {}
