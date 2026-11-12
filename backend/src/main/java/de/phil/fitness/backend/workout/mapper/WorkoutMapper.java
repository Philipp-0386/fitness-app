package de.phil.fitness.backend.workout.mapper;

import de.phil.fitness.backend.exercise.dto.ExerciseSummary;
import de.phil.fitness.backend.workout.dto.*;
import de.phil.fitness.backend.workout.model.Workout;
import de.phil.fitness.backend.workout.repository.WorkoutEntryCounts;
import de.phil.fitness.backend.workout.model.WorkoutExercise;
import de.phil.fitness.backend.workout.model.WorkoutSet;
import de.phil.fitness.backend.workout.repository.LastTimeEntry;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
                workout.getStatus(),
                workout.getStartedAt(),
                workout.getEndedAt(),
                workout.getExercises().stream()
                        .map(we -> toWorkoutExerciseResponse(we, exercises, lastTimes))
                        .toList()
        );
    }

    public WorkoutListItemResponse toWorkoutListItemResponse(Workout workout, List<WorkoutEntryCounts> entries,
                                                             Map<Long, ExerciseSummary> exercises) {
        return new WorkoutListItemResponse(
                workout.getId(),
                workout.getName(),
                workout.getStartedAt(),
                workout.getEndedAt(),
                entries.stream().mapToInt(e -> e.getSetCount().intValue()).sum(),
                entries.stream().map(e -> exercises.get(e.getExerciseId()).name()).toList()
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

    /**
     * @param entries the newest earlier entry per exercise
     * @param sets the sets of those entries, ordered by set number
     * @return last performance keyed by exercise id
     */
    public Map<Long, LastTimeResponse> toLastTimeResponses(List<LastTimeEntry> entries, List<WorkoutSet> sets) {
        Map<Long, List<WorkoutSetResponse>> setsByEntry = sets.stream()
                .collect(Collectors.groupingBy(s -> s.getWorkoutExercise().getId(),
                        Collectors.mapping(this::toWorkoutSetResponse, Collectors.toList())));
        return entries.stream()
                .collect(Collectors.toMap(LastTimeEntry::getExerciseId,
                        e -> new LastTimeResponse(e.getStartedAt(), setsByEntry.getOrDefault(e.getEntryId(), List.of()))));
    }

    public WorkoutSetResponse toWorkoutSetResponse(WorkoutSet set) {
        return new WorkoutSetResponse(
                set.getClientId(),
                set.getSetNumber(),
                set.getSetType(),
                set.getRpe(),
                set.getReps(),
                set.getWeightKg(),
                set.getDurationSeconds(),
                set.getDistanceMeters()
        );
    }
}
