package de.phil.fitness.backend.signup.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

/**
 * Simple DTO responsible for carrying user's data
 */
public record SignUpRequest (
    @NotBlank @Size(min=3, max=32) String username,
    @NotBlank @Email @Size(max=64) String email,
    @NotBlank @Size(min=8, max=72) String password,
    @Size(max=24) String firstName,
    @Size(max=24) String lastName,
    @Past LocalDate dateOfBirth
) {}