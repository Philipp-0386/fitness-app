package de.phil.fitness.backend.routine.dto;

import java.time.Instant;
import java.util.List;

//Routine response that DOES include the (routine) exercises that reference the routine
public record RoutineDetailResponse(
        Long id,
        String name,
        String description,
        Instant createdAt,
        Instant updatedAt,
        List<RoutineExerciseResponse> exercises
) {}
