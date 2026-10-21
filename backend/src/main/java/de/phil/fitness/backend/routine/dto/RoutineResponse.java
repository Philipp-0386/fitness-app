package de.phil.fitness.backend.routine.dto;

import java.time.Instant;

//Routine response that DOES NOT include the list of (routine) exercises that reference the routine, only their count
public record RoutineResponse(
     Long id,
     String name,
     String description,
     int exerciseCount,
     Instant createdAt,
     Instant updatedAt
) {}
