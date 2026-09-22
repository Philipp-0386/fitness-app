package de.phil.fitness.backend.exercise.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Hibernate entity representing a single exercise of the catalog.
 *
 * An exercise with no {@code ownerUserId} is part of the global catalog and visible to everyone.
 * A set owner makes it a user's own definition, visible only to them.
 */
@Entity
@Table(name = "EXERCISE")
@Getter
@Setter
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** {@code null} marks a global exercise; otherwise the id of the owning user. */
    @Column(name = "OWNER_USER_ID")
    private Long ownerUserId;

    @Column(name = "NAME", nullable = false, length = 128)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "EXERCISE_TYPE", nullable = false, length = 16)
    private ExerciseType exerciseType;

    @Column(name = "DESCRIPTION", length = 1000)
    private String description;

    @Column(name = "INSTRUCTIONS")
    private String instructions;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;

    /** Soft delete marker; rows with a value set are excluded from every query. */
    @Column(name = "DELETED_AT")
    private LocalDateTime deletedAt;

    public Exercise() {
    }

    @PrePersist
    public void setTimestampsOnCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void setTimestampOnUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
