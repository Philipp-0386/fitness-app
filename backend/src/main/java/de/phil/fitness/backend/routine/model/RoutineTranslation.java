package de.phil.fitness.backend.routine.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

/**
 * The translated texts of a preset routine in one locale, row of {@code routine_translation}.
 */
@Embeddable
@Getter
public class RoutineTranslation {
    @Column(name = "NAME", nullable = false, length = 128)
    private String name;

    @Column(name = "DESCRIPTION", length = 1000)
    private String description;

    protected RoutineTranslation() {
    }
}
