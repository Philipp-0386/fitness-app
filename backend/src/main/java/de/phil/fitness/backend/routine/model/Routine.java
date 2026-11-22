package de.phil.fitness.backend.routine.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name="ROUTINE")
@Getter
@Setter
public class Routine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "SLUG", length = 128)
    private String slug;

    @Column(name = "NAME", nullable = false, length = 128)
    private String name;

    @Column(name = "DESCRIPTION", length = 1000)
    private String description;

    @Column(name = "CREATED_AT", nullable = false)
    private Instant createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "routine", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    @Setter(AccessLevel.NONE)
    private List<RoutineExercise> exercises = new ArrayList<>();

    /** Translations of a preset routine keyed by locale ({@code "de"}), empty for own routines. */
    @ElementCollection
    @CollectionTable(name = "ROUTINE_TRANSLATION", joinColumns = @JoinColumn(name = "ROUTINE_ID"))
    @MapKeyColumn(name = "LOCALE")
    @Setter(AccessLevel.NONE)
    @BatchSize(size = 100)
    private Map<String, RoutineTranslation> translations = new HashMap<>();

    public void addExercise(RoutineExercise routineExercise) {
        exercises.add(routineExercise);
        routineExercise.setRoutine(this);
    }

    public void clearExercises() {
        exercises.clear();
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
