package de.phil.fitness.backend.program.mapper;

import de.phil.fitness.backend.program.dto.CreateProgramRequest;
import de.phil.fitness.backend.program.dto.ProgramDayResponse;
import de.phil.fitness.backend.program.dto.ProgramDetailResponse;
import de.phil.fitness.backend.program.dto.ProgramResponse;
import de.phil.fitness.backend.program.dto.UpdateProgramDayRequest;
import de.phil.fitness.backend.program.dto.UpdateProgramRequest;
import de.phil.fitness.backend.program.model.Program;
import de.phil.fitness.backend.program.model.ProgramDay;
import de.phil.fitness.backend.routine.dto.RoutineSummary;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ProgramMapper {
    public ProgramResponse toProgramResponse(Program program) {
        return new ProgramResponse(
                program.getId(),
                program.getName(),
                program.getDescription(),
                program.isActive(),
                program.getDays().size(),
                program.getCreatedAt(),
                program.getUpdatedAt()
        );
    }

    public ProgramDetailResponse toProgramDetailResponse(Program program, Map<Long, RoutineSummary> routines) {
        return new ProgramDetailResponse(
                program.getId(),
                program.getName(),
                program.getDescription(),
                program.isActive(),
                program.getCreatedAt(),
                program.getUpdatedAt(),
                program.getDays().stream()
                        .map(day -> toProgramDayResponse(day, routines))
                        .toList()
        );
    }

    private ProgramDayResponse toProgramDayResponse(ProgramDay day, Map<Long, RoutineSummary> routines) {
        RoutineSummary routine = null;
        if (day.getRoutineId() != null) {
            routine = routines.get(day.getRoutineId());
            if (routine == null) {
                throw new IllegalStateException("Program day " + day.getId() + " references missing routine " + day.getRoutineId());
            }
        }
        return new ProgramDayResponse(day.getId(), day.getOrderIndex(), routine);
    }

    public Program toProgramEntity(CreateProgramRequest request, Long userId) {
        Program program = new Program();
        program.setUserId(userId);
        program.setName(request.name());
        program.setDescription((request.description()));

        for(int i = 0; i < request.days().size(); i++) {
            ProgramDay day = new ProgramDay();
            day.setRoutineId(request.days().get(i).routineId());
            day.setOrderIndex(i);
            program.addDay(day);
        }
        return program;
    }

    /**
     * Applies the new state to a program. Days keep their id, so workouts stay linked to them: a day with an id is
     * updated, a day without one is added, and a day missing from the request is removed.
     * The caller has to make sure every id in the request belongs to this program.
     */
    public void applyUpdateOfUserProgram(Program program, UpdateProgramRequest request) {
        program.setName(request.name());
        program.setDescription(request.description());
        program.setUpdatedAt(Instant.now());

        Map<Long, ProgramDay> existing = program.getDays().stream()
                .collect(Collectors.toMap(ProgramDay::getId, Function.identity()));
        Set<Long> kept = request.days().stream()
                .map(UpdateProgramDayRequest::id)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        program.getDays().removeIf(day -> !kept.contains(day.getId()));

        for (int i = 0; i < request.days().size(); i++) {
            UpdateProgramDayRequest dayRequest = request.days().get(i);
            ProgramDay day;
            if (dayRequest.id() == null) {
                day = new ProgramDay();
                program.addDay(day);
            } else {
                day = existing.get(dayRequest.id());
            }
            day.setRoutineId(dayRequest.routineId());
            day.setOrderIndex(i);
        }
        program.getDays().sort(Comparator.comparing(ProgramDay::getOrderIndex));
    }
}
