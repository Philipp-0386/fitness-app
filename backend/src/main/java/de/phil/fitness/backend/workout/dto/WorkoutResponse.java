package de.phil.fitness.backend.workout.dto;

import java.time.Instant;
import java.util.List;

public record WorkoutResponse(
        Long id,
        String name,
        Long routineId,
        Long programDayId,
        Instant startedAt,
        List<WorkoutExerciseResponse> exercises
) {}
