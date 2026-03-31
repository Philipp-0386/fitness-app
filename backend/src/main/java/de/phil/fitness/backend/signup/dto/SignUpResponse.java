package de.phil.fitness.backend.signup.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Contains shortened version of user's data.
 */
@Getter
@Setter
public class SignUpResponse {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
}
