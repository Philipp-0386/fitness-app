package de.phil.fitness.backend.program.dto;

import de.phil.fitness.backend.routine.dto.RoutineSummary;

//routine is null for a rest day
public record ProgramDayResponse(
        Long id,
        Integer orderIndex,
        RoutineSummary routine
)
{}
