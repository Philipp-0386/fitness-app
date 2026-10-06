package de.phil.fitness.backend.routine.mapper;

import de.phil.fitness.backend.routine.dto.RoutineResponse;
import de.phil.fitness.backend.routine.model.Routine;
import de.phil.fitness.backend.routine.repository.RoutineRepository;
import org.springframework.stereotype.Component;

@Component
public class RoutineMapper {
    public RoutineMapper() { }

    public RoutineResponse toRoutineResponse(Routine routine) {
        return new RoutineResponse(
                routine.getId(),
                routine.getName(),
                routine.getDescription(),
                routine.getCreatedAt(),
                routine.getUpdatedAt()
        );
    }
}
