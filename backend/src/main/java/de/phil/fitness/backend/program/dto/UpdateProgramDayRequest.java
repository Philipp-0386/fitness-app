package de.phil.fitness.backend.program.dto;

//id is null for a new day, routineId is null for a rest day
public record UpdateProgramDayRequest(
        Long id,
        Long routineId
) {}
