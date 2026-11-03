package de.phil.fitness.backend.workout.mapper;

import de.phil.fitness.backend.exercise.dto.ExerciseSummary;
import de.phil.fitness.backend.workout.dto.LastTimeResponse;
import de.phil.fitness.backend.workout.dto.WorkoutExerciseResponse;
import de.phil.fitness.backend.workout.dto.WorkoutResponse;
import de.phil.fitness.backend.workout.dto.WorkoutSetResponse;
import de.phil.fitness.backend.workout.model.Workout;
import de.phil.fitness.backend.workout.model.WorkoutExercise;
import de.phil.fitness.backend.workout.model.WorkoutSet;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class WorkoutMapper {
    public WorkoutMapper() { }

    /**
     * @param exercises summaries of every exercise in the workout, keyed by exercise id
     * @param lastTimes last performance keyed by exercise id, exercises without one are missing
     */
    public WorkoutResponse toWorkoutResponse(Workout workout, Map<Long, ExerciseSummary> exercises,
                                             Map<Long, LastTimeResponse> lastTimes) {
        return new WorkoutResponse(
                workout.getId(),
                workout.getName(),
                workout.getRoutineId(),
                workout.getProgramDayId(),
                workout.getStartedAt(),
                workout.getExercises().stream()
                        .map(we -> toWorkoutExerciseResponse(we, exercises, lastTimes))
                        .toList()
        );
    }

    public WorkoutExerciseResponse toWorkoutExerciseResponse(WorkoutExercise we, Map<Long, ExerciseSummary> exercises,
                                                             Map<Long, LastTimeResponse> lastTimes) {
        ExerciseSummary exercise = exercises.get(we.getExerciseId());
        if (exercise == null) {
            throw new IllegalStateException("Workout exercise " + we.getId() + " references missing exercise " + we.getExerciseId());
        }
        return new WorkoutExerciseResponse(
                we.getId(),
                we.getOrderIndex(),
                exercise,
                we.getTargetSetsSnapshot(),
                we.getTargetRepsMinSnapshot(),
                we.getTargetRepsMaxSnapshot(),
                we.getTargetDurationSecondsSnapshot(),
                we.getTargetDistanceMetersSnapshot(),
                we.getSets().stream()
                        .map(this::toWorkoutSetResponse)
                        .toList(),
                lastTimes.get(we.getExerciseId())
        );
    }

    public WorkoutSetResponse toWorkoutSetResponse(WorkoutSet set) {
        return new WorkoutSetResponse(
                set.getClientId(),
                set.getSetNumber(),
                set.getSetType(),
                set.getReps(),
                set.getWeightKg(),
                set.getDurationSeconds(),
                set.getDistanceMeters()
        );
    }
}
