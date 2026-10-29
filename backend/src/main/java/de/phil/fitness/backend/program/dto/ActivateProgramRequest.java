package de.phil.fitness.backend.program.dto;

import jakarta.validation.constraints.NotNull;

public record ActivateProgramRequest(
        @NotNull Long programId
) {}
