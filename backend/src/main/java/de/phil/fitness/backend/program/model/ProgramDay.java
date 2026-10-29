package de.phil.fitness.backend.program.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "PROGRAM_DAY")
@Getter
@Setter
public class ProgramDay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "USER_ID")
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PROGRAM_ID", nullable = false)
    private Program program;

    @Column(name = "ROUTINE_ID")
    private Long routineId;

    @Column(name = "ORDER_INDEX", nullable = false)
    private Integer orderIndex;
}
