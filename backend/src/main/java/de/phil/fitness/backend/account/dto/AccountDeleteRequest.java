package de.phil.fitness.backend.account.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * @param password Confirms the deletion, must match the stored password
 */
public record AccountDeleteRequest(
        @NotBlank String password
) {}
