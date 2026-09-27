package de.phil.fitness.backend.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * @param username New login name, may equal the current one
 * @param email New email address, may equal the current one
 * @param currentPassword Confirms the change, must match the stored password
 */
public record UserUpdateRequest(
        @NotBlank @Size(min=3, max=32) String username,
        @NotBlank @Email @Size(max=64) String email,
        @NotBlank String currentPassword
) {}
