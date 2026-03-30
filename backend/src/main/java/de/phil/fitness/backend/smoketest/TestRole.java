package de.phil.fitness.backend.smoketest;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ROLES")
@Getter
@Setter
public class TestRole {
    @Id
    @Column(name = "ID")
    private Integer roleId;

    @Column(name = "NAME", nullable = false, unique = true)
    private String roleName;
}
