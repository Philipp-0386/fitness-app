package de.phil.fitness.backend.routine.dto;

//Used by the "program" slice as a compact view of the routine of a program day.
public record RoutineSummary(
        Long id,
        String name
) {}
