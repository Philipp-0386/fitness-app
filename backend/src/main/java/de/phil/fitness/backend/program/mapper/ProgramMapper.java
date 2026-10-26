package de.phil.fitness.backend.program.mapper;

import de.phil.fitness.backend.program.dto.ProgramDayResponse;
import de.phil.fitness.backend.program.dto.ProgramDetailResponse;
import de.phil.fitness.backend.program.dto.ProgramResponse;
import de.phil.fitness.backend.program.model.Program;
import de.phil.fitness.backend.program.model.ProgramDay;
import de.phil.fitness.backend.routine.dto.RoutineSummary;
import org.springframework.stereotype.Component;

import java.util.Map;

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
}
