package de.phil.fitness.backend.exercise.dto;

import jakarta.validation.constraints.*;

public record ExerciseRequest(
        @NotBlank @Size(min=1, max=32) String name,
        @NotBlank String type,
        String description,
        String instructions
) {}
