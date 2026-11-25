package de.phil.fitness.backend.exercise.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

/**
 * The translated texts of a global exercise in one locale, row of {@code exercise_translation}.
 */
@Embeddable
@Getter
public class ExerciseTranslation {
    @Column(name = "NAME", nullable = false, length = 128)
    private String name;

    @Column(name = "DESCRIPTION", length = 1000)
    private String description;

    @Column(name = "INSTRUCTIONS")
    private String instructions;

    protected ExerciseTranslation() {
    }
}
