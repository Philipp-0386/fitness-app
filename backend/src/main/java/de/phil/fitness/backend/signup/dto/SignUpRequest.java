package de.phil.fitness.backend.signup.dto;

import de.phil.fitness.backend.signup.model.Role;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Simple DTO responsible for carrying user's data.
 */
@Getter
@Setter
public class SignUpRequest {
    private final Integer id = null;
    private String email;
    private String passwordUnhashed;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private final Role role = null;
    private LocalDateTime createdAt;
}