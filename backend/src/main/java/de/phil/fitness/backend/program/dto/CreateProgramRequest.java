package de.phil.fitness.backend.program.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateProgramRequest(
    @NotBlank @Size(max = 128) String name,
    @Size(max = 1000) String description,
    @NotNull @Size(min = 1, max = 14) List<@NotNull @Valid ProgramDayRequest> days
) {
    @AssertTrue(message = "program needs minimum one training day on creation")
    public boolean isTrainingDayPresent() {
        return days == null || days.stream().anyMatch(day -> day != null && day.routineId() != null);
    }
}
