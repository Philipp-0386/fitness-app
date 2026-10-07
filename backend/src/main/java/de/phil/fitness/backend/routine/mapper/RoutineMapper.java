package de.phil.fitness.backend.routine.mapper;

import de.phil.fitness.backend.exercise.dto.ExerciseSummary;
import de.phil.fitness.backend.routine.dto.*;
import de.phil.fitness.backend.routine.model.Routine;
import de.phil.fitness.backend.routine.model.RoutineExercise;
import org.springframework.stereotype.Component;

import java.util.List;
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

    public Routine toRoutineEntity(CreateRoutineRequest request, Long userId) {
        Routine routine = new Routine();
        routine.setUserId(userId);
        routine.setName(request.name());
        routine.setDescription(request.description());

        List<RoutineExerciseRequest> exercises;
        if(request.exercises() == null) {
            exercises = List.of();
        } else {
            exercises = request.exercises();
        }

        for (int i = 0; i < exercises.size(); i++) {
            routine.addExercise(toRoutineExerciseEntity(exercises.get(i), i));
        }
        return routine;
    }

    private RoutineExercise toRoutineExerciseEntity(RoutineExerciseRequest request, int index) {
        RoutineExercise exercise = new RoutineExercise();
        exercise.setExerciseId(request.exerciseId());
        exercise.setOrderIndex(index);
        exercise.setTargetSets(request.targetSets());
        exercise.setTargetRepsMin(request.targetRepsMin());
        exercise.setTargetRepsMax(request.targetRepsMax());
        exercise.setTargetRpe(request.targetRpe());
        exercise.setTargetDurationSeconds(request.targetDurationSeconds());
        exercise.setTargetDistanceMeters(request.targetDistanceMeters());
        return exercise;
    }

}
