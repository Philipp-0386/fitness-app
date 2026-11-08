package de.phil.fitness.backend.workout.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "WORKOUT_SET")
@Getter
@Setter
public class WorkoutSet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "WORKOUT_EXERCISE_ID", nullable = false)
    private WorkoutExercise workoutExercise;

    @Column(name = "CLIENT_ID", nullable = false)
    private UUID clientId;

    @Column(name = "SET_NUMBER", nullable = false)
    private Integer setNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "SET_TYPE", nullable = false, length = 16)
    private SetType setType;

    @Column(name = "REPS")
    private Integer reps;

    @Column(name = "WEIGHT_KG", precision = 6, scale = 2)
    private BigDecimal weightKg;

    @Column(name = "RPE", precision = 3, scale = 1)
    private BigDecimal rpe;

    @Column(name = "DURATION_SECONDS")
    private Integer durationSeconds;

    @Column(name = "DISTANCE_METERS", precision = 8, scale = 2)
    private BigDecimal distanceMeters;

    @Column(name = "NOTES", length = 1000)
    private String notes;

    @Column(name = "CREATED_AT", nullable = false)
    private Instant createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    private Instant updatedAt;

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
