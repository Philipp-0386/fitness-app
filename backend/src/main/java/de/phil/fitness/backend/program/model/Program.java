package de.phil.fitness.backend.program.model;

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
@Table(name = "PROGRAM")
@Getter
@Setter
public class Program {
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

    @Column(name = "IS_ACTIVE", nullable = false)
    private boolean active;

    @Column(name = "CREATED_AT", nullable = false)
    private Instant createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    @Setter(AccessLevel.NONE)
    private List<ProgramDay> days = new ArrayList<>();

    /** Translations of a preset program keyed by locale ({@code "de"}), empty for own programs. */
    @ElementCollection
    @CollectionTable(name = "PROGRAM_TRANSLATION", joinColumns = @JoinColumn(name = "PROGRAM_ID"))
    @MapKeyColumn(name = "LOCALE")
    @Setter(AccessLevel.NONE)
    @BatchSize(size = 100)
    private Map<String, ProgramTranslation> translations = new HashMap<>();

    public void addDay(ProgramDay programDay) {
        days.add(programDay);
        programDay.setProgram(this);
        programDay.setUserId(userId);
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
