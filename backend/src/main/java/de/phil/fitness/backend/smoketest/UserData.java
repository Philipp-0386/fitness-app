package de.phil.fitness.backend.smoketest;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "USERDATA")
@Getter
@Setter
public class UserData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "USERNAME", nullable = false, unique = true, length = 32)
    private String username;

    @Column(name = "EMAIL", nullable = false, unique = true, length = 36)
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

    public UserData() {

    }
    //Getter: Lombok generated
}
