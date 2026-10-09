package de.phil.fitness.backend.routine.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdateRoutineRequest(
        @NotBlank @Size(max = 128) String name,
        @Size(max = 1000) String description,
        @Valid @Size(max = 50) List<RoutineExerciseRequest> exercises
)
{}
