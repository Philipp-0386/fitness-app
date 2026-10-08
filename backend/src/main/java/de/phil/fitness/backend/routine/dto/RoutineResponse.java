package de.phil.fitness.backend.routine.dto;

import java.time.Instant;

//Routine response that DOES NOT include the list of (routine) exercises that reference the routine
public record RoutineResponse(
     Long id,
     String name,
     String description,
     Instant createdAt,
     Instant updatedAt
) {}
