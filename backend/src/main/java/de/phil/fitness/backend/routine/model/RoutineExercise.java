package de.phil.fitness.backend.routine.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "ROUTINE_EXERCISE")
@Getter
@Setter
public class RoutineExercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ROUTINE_ID", nullable = false)
    private Routine routine;

    @Column(name = "EXERCISE_ID", nullable = false)
    private Long exerciseId;

    @Column(name = "ORDER_INDEX", nullable = false)
    private Integer orderIndex;

    @Column(name = "TARGET_SETS")
    private Integer targetSets;

    @Column(name = "TARGET_REPS_MIN")
    private Integer targetRepsMin;

    @Column(name = "TARGET_REPS_MAX")
    private Integer targetRepsMax;

    @Column(name = "TARGET_RPE", precision = 3, scale = 1)
    private BigDecimal targetRpe;

    @Column(name = "TARGET_DURATION_SECONDS")
    private Integer targetDurationSeconds;

    @Column(name = "TARGET_DISTANCE_METERS", precision = 8, scale = 2)
    private BigDecimal targetDistanceMeters;

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
