package de.phil.fitness.backend.exercise.model;

import jakarta.persistence.*;
import lombok.Getter;

/**
 * A muscle group an exercise trains, row of {@code exercise_musclegroup}.
 */
@Embeddable
@Getter
public class ExerciseMuscle {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MUSCLE_GROUP_ID", nullable = false)
    private MuscleGroup muscleGroup;

    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE", nullable = false, length = 16)
    private MuscleRole role;
}
