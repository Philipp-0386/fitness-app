package de.phil.fitness.backend.exercise.dto;

import de.phil.fitness.backend.exercise.model.ExerciseType;
import de.phil.fitness.backend.exercise.model.TrackingType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public record UpdateExerciseRequest(
        @NotBlank @Size(max = 128) String name,
        @NotNull ExerciseType exerciseType,
        @NotNull TrackingType trackingType,
        @Size(max = 1000) String description,
        @Size(max = 4000) String instructions,
        @Size(max = 10) List<@NotNull @Valid ExerciseMuscleRequest> muscles
) {
    @AssertTrue(message = "a muscle may appear only once")
    public boolean isMuscleIdsUnique() {
        if (muscles == null) return true;
        List<Long> ids = muscles.stream()
                .filter(Objects::nonNull)
                .map(ExerciseMuscleRequest::muscleGroupId)
                .filter(Objects::nonNull)
                .toList();
        return ids.size() == new HashSet<>(ids).size();
    }
}
