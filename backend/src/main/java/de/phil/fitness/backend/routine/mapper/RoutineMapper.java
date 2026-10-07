package de.phil.fitness.backend.routine.mapper;

import de.phil.fitness.backend.exercise.dto.ExerciseSummary;
import de.phil.fitness.backend.routine.dto.RoutineDetailResponse;
import de.phil.fitness.backend.routine.dto.RoutineExerciseResponse;
import de.phil.fitness.backend.routine.dto.RoutineResponse;
import de.phil.fitness.backend.routine.model.Routine;
import de.phil.fitness.backend.routine.model.RoutineExercise;
import de.phil.fitness.backend.routine.repository.RoutineRepository;
import org.springframework.stereotype.Component;

import java.util.Map;

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

    public RoutineDetailResponse toRoutineDetailResponse(Routine routine, Map<Long, ExerciseSummary> exercises) {
        return new RoutineDetailResponse(
                routine.getId(),
                routine.getName(),
                routine.getDescription(),
                routine.getCreatedAt(),
                routine.getUpdatedAt(),
                routine.getExercises().stream()
                        .map(re -> toRoutineExerciseResponse(re, exercises))
                        .toList()
        );
    }

    private RoutineExerciseResponse toRoutineExerciseResponse(RoutineExercise re, Map<Long, ExerciseSummary> exercises) {
        ExerciseSummary exercise = exercises.get(re.getExerciseId());
        if (exercise == null) {
            throw new IllegalStateException("Routine exercise " + re.getId() + " references missing exercise " + re.getExerciseId());
        }
        return new RoutineExerciseResponse(
                re.getId(),
                re.getOrderIndex(),
                exercise,
                re.getTargetSets(),
                re.getTargetRepsMin(),
                re.getTargetRepsMax(),
                re.getTargetRpe(),
                re.getTargetDurationSeconds(),
                re.getTargetDistanceMeters()
        );
    }

}
