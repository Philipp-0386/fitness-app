package de.phil.fitness.backend.user.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicInsert;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

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
}
