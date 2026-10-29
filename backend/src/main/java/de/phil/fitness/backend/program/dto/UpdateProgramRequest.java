package de.phil.fitness.backend.program.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public record UpdateProgramRequest(
    @NotBlank @Size(max = 128) String name,
    @Size(max = 1000) String description,
    @NotNull @Size(min = 1, max = 14) List<@NotNull @Valid UpdateProgramDayRequest> days
) {
    @AssertTrue(message = "program needs minimum one training day")
    public boolean isTrainingDayPresent() {
        return days == null || days.stream().anyMatch(day -> day != null && day.routineId() != null);
    }

    @AssertTrue(message = "a day id may appear only once")
    public boolean isDayIdsUnique() {
        if (days == null) return true;
        List<Long> ids = days.stream()
                .filter(Objects::nonNull)
                .map(UpdateProgramDayRequest::id)
                .filter(Objects::nonNull)
                .toList();
        return ids.size() == new HashSet<>(ids).size();
    }
}
