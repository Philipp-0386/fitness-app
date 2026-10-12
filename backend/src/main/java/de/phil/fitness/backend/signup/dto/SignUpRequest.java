package de.phil.fitness.backend.signup.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Simple DTO responsible for carrying user's data
 */
public record SignUpRequest (
    @NotBlank(message = "Username is required")
    @Size(min=5, max=32, message = "Username must be between {min} and {max} characters long")
    String username,
    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email address")
    @Size(max=64, message = "Email must be at most {max} characters long")
    String email,
    @NotBlank(message = "Password is required")
    @Size(min=8, max=72, message = "Password must be between {min} and {max} characters long")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
            message = "Password must contain at least one uppercase letter, one lowercase letter and one number")
    String password
) {}
