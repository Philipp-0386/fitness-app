package de.phil.fitness.backend.routine.dto;

import java.time.Instant;

public record RoutineResponse(
     Long id,
     String name,
     String description,
     Instant createdAt,
     Instant updatedAt
) {}
