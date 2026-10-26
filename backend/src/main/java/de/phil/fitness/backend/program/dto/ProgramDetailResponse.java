package de.phil.fitness.backend.program.dto;

import java.time.Instant;
import java.util.List;

//Program response that DOES include its days
public record ProgramDetailResponse(
        Long id,
        String name,
        String description,
        boolean active,
        Instant createdAt,
        Instant updatedAt,
        List<ProgramDayResponse> days
) {}
