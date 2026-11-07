package de.phil.fitness.backend.workout.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "WORKOUT_EXERCISE")
@Getter
@Setter
public class WorkoutExercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "WORKOUT_ID", nullable = false)
    private Workout workout;

    @Column(name = "EXERCISE_ID", nullable = false)
    private Long exerciseId;

    @Column(name = "ORDER_INDEX", nullable = false)
    private Integer orderIndex;

    @Column(name = "NOTES", length = 1000)
    private String notes;

    @Column(name = "TARGET_SETS_SNAPSHOT")
    private Integer targetSetsSnapshot;

    @Column(name = "TARGET_REPS_MIN_SNAPSHOT")
    private Integer targetRepsMinSnapshot;

    @Column(name = "TARGET_REPS_MAX_SNAPSHOT")
    private Integer targetRepsMaxSnapshot;

    @Column(name = "TARGET_DURATION_SECONDS_SNAPSHOT")
    private Integer targetDurationSecondsSnapshot;

    @Column(name = "TARGET_DISTANCE_METERS_SNAPSHOT", precision = 8, scale = 2)
    private BigDecimal targetDistanceMetersSnapshot;

    @Column(name = "CREATED_AT", nullable = false)
    private Instant createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "workoutExercise", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("setNumber ASC")
    @Setter(AccessLevel.NONE)
    private List<WorkoutSet> sets = new ArrayList<>();

    public void addSet(WorkoutSet workoutSet) {
        sets.add(workoutSet);
        workoutSet.setWorkoutExercise(this);
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
