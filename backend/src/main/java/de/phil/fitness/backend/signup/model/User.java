package de.phil.fitness.backend.signup.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Hibernate entity representing user entries.
 */
@Entity
@DynamicInsert
@Table(name = "USERDATA")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "USERNAME", nullable = false, unique = true, length = 32)
    private String username;

    @Column(name = "PASSWORD_HASHED", nullable = false, unique = false, length = 128)
    private String passwordHashed;

    @Column(name = "EMAIL", nullable = false, unique = true, length = 64)
    private String email;

    @Column(name = "FIRST_NAME", length = 24)
    private String firstName;

    @Column(name = "LAST_NAME", length = 24)
    private String lastName;

    @Column(name = "DATE_OF_BIRTH", nullable = false)
    private LocalDate dateOfBirth;

    @ManyToOne
    @JoinColumn(name = "ROLE_ID", nullable = false)
    private Role role;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    public User() {

    }

    @PrePersist
    public void setCreatedAt() {
        this.createdAt = LocalDateTime.now();
    }

    //Getter: Lombok generated
}

