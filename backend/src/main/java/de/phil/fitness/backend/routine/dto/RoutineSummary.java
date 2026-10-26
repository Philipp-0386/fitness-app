package de.phil.fitness.backend.routine.dto;

import java.util.List;

//Used by the "program" slice as a compact view of the routine of a program day.
public record RoutineSummary(
        Long id,
        String name,
        List<String> exerciseNames
) {}
