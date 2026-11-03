package de.phil.fitness.backend.workout.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "WORKOUT")
@Getter
@Setter
public class Workout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "USER_ID", nullable = false)
    private Long userId;

    @Column(name = "ROUTINE_ID")
    private Long routineId;

    @Column(name = "PROGRAM_DAY_ID")
    private Long programDayId;

    @Column(name = "NAME", nullable = false, length = 128)
    private String name;

    @Column(name = "STARTED_AT", nullable = false)
    private Instant startedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 16)
    private WorkoutStatus status;

    @Column(name = "ENDED_AT")
    private Instant endedAt;

    @Column(name = "NOTES", length = 1000)
    private String notes;

    @Column(name = "CREATED_AT", nullable = false)
    private Instant createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "workout", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    @Setter(AccessLevel.NONE)
    private List<WorkoutExercise> exercises = new ArrayList<>();

    public void addExercise(WorkoutExercise workoutExercise) {
        exercises.add(workoutExercise);
        workoutExercise.setWorkout(this);
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
