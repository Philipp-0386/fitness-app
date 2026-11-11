package de.phil.fitness.backend.user.dto;

import de.phil.fitness.backend.user.model.DistanceUnit;
import de.phil.fitness.backend.user.model.WeightUnit;
import de.phil.fitness.backend.user.validation.ValidTimeZone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Replaces all settings of the authenticated user.
 *
 * @param weightUnit Unit in which weights are displayed
 * @param distanceUnit Unit in which distances are displayed
 * @param timeZone IANA region id, e.g. {@code Europe/Berlin}
 */
public record UserSettingsUpdateRequest(
        @NotNull(message = "Weight unit is required")
        WeightUnit weightUnit,
        @NotNull(message = "Distance unit is required")
        DistanceUnit distanceUnit,
        @NotBlank(message = "Time zone is required")
        @ValidTimeZone
        String timeZone
) {}
