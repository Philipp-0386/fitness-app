package de.phil.fitness.backend.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * @param currentPassword Confirms the change, must match the stored password
 * @param newPassword Replaces the stored password
 */
public record PasswordUpdateRequest(
        @NotBlank String currentPassword,
        @NotBlank @Size(min=8, max=72) @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$") String newPassword
) {}
