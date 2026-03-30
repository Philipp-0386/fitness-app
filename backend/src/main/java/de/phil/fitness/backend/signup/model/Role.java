package de.phil.fitness.backend.signup.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Hibernate entity representing role entries.
 *
 * <p>For user creation process only used to have an existing reference while creating a new user.
 */
@Entity
@Table(name = "ROLES")
@Getter
@Setter
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "NAME", nullable = false, unique = true)
    private String name;
}
