package de.phil.fitness.backend.workout.dto;

import jakarta.validation.constraints.AssertTrue;

public record StartWorkoutRequest(
        Long routineId,
        Long programDayId
) {
    @AssertTrue(message = "exactly one of routineId and programDayId is required")
    public boolean isExactlyOneSourceGiven() {
        return (routineId == null) != (programDayId == null);
    }
}
