package de.phil.fitness.backend.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * @param currentPassword Confirms the change, must match the stored password
 * @param newPassword Replaces the stored password
 */
public record PasswordUpdateRequest(
        @NotBlank(message = "Current password is required") String currentPassword,
        @NotBlank(message = "New password is required")
        @Size(min=8, max=72, message = "Password must be between {min} and {max} characters long")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
                message = "Password must contain at least one uppercase letter, one lowercase letter and one number")
        String newPassword
) {}
