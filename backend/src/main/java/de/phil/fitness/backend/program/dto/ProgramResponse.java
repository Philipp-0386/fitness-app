package de.phil.fitness.backend.program.dto;

import java.time.Instant;

//Program response that DOES NOT include its days, only their count
public record ProgramResponse(
        Long id,
        String name,
        String description,
        boolean active,
        int dayCount,
        Instant createdAt,
        Instant updatedAt
) {}
