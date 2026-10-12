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
        @NotBlank(message = "Username is required")
        @Size(min=5, max=32, message = "Username must be between {min} and {max} characters long")
        String username,
        @NotBlank(message = "Email is required")
        @Email(message = "Please enter a valid email address")
        @Size(max=64, message = "Email must be at most {max} characters long")
        String email,
        @NotBlank(message = "Password is required") String currentPassword
) {}
