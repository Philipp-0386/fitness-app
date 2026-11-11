package de.phil.fitness.backend.user.dto;

import de.phil.fitness.backend.user.model.DistanceUnit;
import de.phil.fitness.backend.user.model.WeightUnit;

/**
 * The settings of the authenticated user as returned to the client.
 *
 * @param weightUnit Unit in which weights are displayed
 * @param distanceUnit Unit in which distances are displayed
 * @param timeZone IANA region id, e.g. {@code Europe/Berlin}
 */
public record UserSettingsResponse(
        WeightUnit weightUnit,
        DistanceUnit distanceUnit,
        String timeZone
) {}
