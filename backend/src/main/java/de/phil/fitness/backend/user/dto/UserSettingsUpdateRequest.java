package de.phil.fitness.backend.user.dto;

import de.phil.fitness.backend.user.model.DistanceUnit;
import de.phil.fitness.backend.user.model.Language;
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
 * @param language Language of the UI and of the translated catalog
 */
public record UserSettingsUpdateRequest(
        @NotNull WeightUnit weightUnit,
        @NotNull DistanceUnit distanceUnit,
        @NotBlank @ValidTimeZone String timeZone,
        @NotNull Language language
) {}
