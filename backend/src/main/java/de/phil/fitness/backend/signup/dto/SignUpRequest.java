package de.phil.fitness.backend.signup.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import de.phil.fitness.backend.user.model.Role;
import lombok.Getter;
import lombok.Setter;

/**
 * Simple DTO responsible for carrying user's data.
 */
@Getter
@Setter
public class SignUpRequest {
    private final Integer id = null;
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private final Role role = null;
    private LocalDateTime createdAt;
}