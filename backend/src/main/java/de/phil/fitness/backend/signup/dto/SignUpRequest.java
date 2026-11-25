package de.phil.fitness.backend.signup.dto;

import de.phil.fitness.backend.user.model.Language;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Simple DTO responsible for carrying user's data
 *
 * @param language Language the guest used while signing up, stored as the account's language. Optional, {@code EN} if absent.
 */
public record SignUpRequest (
    @NotBlank @Size(min=5, max=32) String username,
    @NotBlank @Email @Size(max=64) String email,
    @NotBlank @Size(min=8, max=72) @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$") String password,
    Language language
) {}
