package de.phil.fitness.backend.signup.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    @Column(name = "ROLE_ID")
    private Integer roleId;

    @Column(name = "ROLE_NAME", nullable = false, unique = true)
    private String roleName;
}
