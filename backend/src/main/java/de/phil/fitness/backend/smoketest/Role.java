package de.phil.fitness.backend.smoketest;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
