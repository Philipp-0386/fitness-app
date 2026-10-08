package de.phil.fitness.backend.routine.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public record CreateRoutineRequest(
        @NotBlank @Size(max = 128) String name,
        @Size(max = 1000) String description,
        @Valid @Size(max = 50) List<RoutineExerciseRequest> exercises
) {}
