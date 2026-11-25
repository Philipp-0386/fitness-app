package de.phil.fitness.backend.program.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

/**
 * The translated texts of a preset program in one locale, row of {@code program_translation}.
 */
@Embeddable
@Getter
public class ProgramTranslation {
    @Column(name = "NAME", nullable = false, length = 128)
    private String name;

    @Column(name = "DESCRIPTION", length = 1000)
    private String description;

    protected ProgramTranslation() {
    }
}
