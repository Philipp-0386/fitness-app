package de.phil.fitness.backend.user.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * @param password Confirms the deletion, must match the stored password
 */
public record UserDeleteRequest(
        @NotBlank String password
) {}
