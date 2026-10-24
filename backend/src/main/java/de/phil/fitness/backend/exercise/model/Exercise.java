package de.phil.fitness.backend.exercise.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "TRACKING_TYPE", nullable = false, length = 24)
    private TrackingType trackingType;

    @Column(name = "DESCRIPTION", length = 1000)
    private String description;

    @Column(name = "INSTRUCTIONS")
    private String instructions;

    @Column(name = "CREATED_AT", nullable = false)
    private Instant createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    private Instant updatedAt;

    /** Soft delete marker; rows with a value set are excluded from every query. */
    @Column(name = "DELETED_AT")
    private Instant deletedAt;

    @ElementCollection
    @CollectionTable(name = "EXERCISE_MUSCLEGROUP", joinColumns = @JoinColumn(name = "EXERCISE_ID"))
    @Setter(AccessLevel.NONE)
    @BatchSize(size = 100)
    private List<ExerciseMuscle> muscles = new ArrayList<>();

    public Exercise() {
    }

    @PrePersist
    public void setTimestampsOnCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void setTimestampOnUpdate() {
        this.updatedAt = Instant.now();
    }
}
