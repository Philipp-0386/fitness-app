package de.phil.fitness.backend.workout.dto;

import de.phil.fitness.backend.workout.model.SetType;

import java.math.BigDecimal;
import java.util.UUID;

public record WorkoutSetResponse(
        UUID clientId,
        Integer setNumber,
        SetType setType,
        BigDecimal rpe,
        Integer reps,
        BigDecimal weightKg,
        Integer durationSeconds,
        BigDecimal distanceMeters
) {}
