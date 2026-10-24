package de.phil.fitness.backend.exercise.model;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

/**
 * Read only lookup of muscle groups in two levels, filled by the reference data migration.
 *
 * A group (Chest) has a {@code bodyRegion} and no {@code parent}, an area (Upper Chest) has a {@code parent} and no
 * {@code bodyRegion}.
 */
@Entity
@Immutable
@Table(name = "MUSCLE_GROUP")
@Getter
@BatchSize(size = 100)
public class MuscleGroup {
    @Id
    private Long id;

    /** {@code null} for a group, otherwise the group this area belongs to. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ID")
    private MuscleGroup parent;

    @Column(name = "NAME", nullable = false, length = 64)
    private String name;

    /** Only set for a group. */
    @Enumerated(EnumType.STRING)
    @Column(name = "BODY_REGION", length = 16)
    private BodyRegion bodyRegion;

    protected MuscleGroup() {
    }
}
