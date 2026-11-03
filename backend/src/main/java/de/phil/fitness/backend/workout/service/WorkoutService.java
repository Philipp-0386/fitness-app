package de.phil.fitness.backend.workout.service;

import de.phil.fitness.backend.exercise.dto.ExerciseSummary;
import de.phil.fitness.backend.exercise.service.ExerciseService;
import de.phil.fitness.backend.program.service.ProgramService;
import de.phil.fitness.backend.routine.dto.RoutineDetailResponse;
import de.phil.fitness.backend.routine.dto.RoutineExerciseResponse;
import de.phil.fitness.backend.routine.service.RoutineService;
import de.phil.fitness.backend.workout.dto.StartWorkoutRequest;
import de.phil.fitness.backend.workout.dto.WorkoutResponse;
import de.phil.fitness.backend.workout.exception.*;
import de.phil.fitness.backend.workout.mapper.WorkoutMapper;
import de.phil.fitness.backend.workout.model.Workout;
import de.phil.fitness.backend.workout.model.WorkoutExercise;
import de.phil.fitness.backend.workout.model.WorkoutStatus;
import de.phil.fitness.backend.workout.repository.WorkoutRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class WorkoutService {
    private final WorkoutRepository workoutRepository;
    private final WorkoutMapper workoutMapper;

    private final ExerciseService exerciseService;
    private final RoutineService routineService;
    private final ProgramService programService;

    private static final Duration STALE_AFTER = Duration.ofHours(4); //auto-close var

    public WorkoutService(WorkoutRepository workoutRepository,  WorkoutMapper workoutMapper,
                          ExerciseService exerciseService, RoutineService routineService, ProgramService programService) {
        this.workoutRepository = workoutRepository;
        this.workoutMapper = workoutMapper;
        this.exerciseService = exerciseService;
        this.routineService = routineService;
        this.programService = programService;
    }

    /**
     * Retrieves the workout the user has in progress. A workout without activity (start or newest set) for longer than
     * {@link #STALE_AFTER} is closed on the way: completed at the time of its newest set, or deleted if it has no sets.
     * Flushes after closing, so a start in the same transaction does not collide with the unique index on running
     * workouts.
     * @param userId Id of the requesting user.
     * @return The running workout with its exercises and sets, empty if the user has none or it was just closed.
     */
    @Transactional
    public Optional<WorkoutResponse> findActiveWorkout(Long userId) {
        Optional<Workout> found = workoutRepository.getActiveUserWorkout(userId);
        if(found.isEmpty()) {
            return Optional.empty();
        }
        Workout workout = found.get();

        Instant newestSet = workoutRepository.findNewestSetCreatedAt(workout.getId());
        Instant lastActivity = newestSet != null ? newestSet : workout.getStartedAt();

        if (lastActivity.isBefore(Instant.now().minus(STALE_AFTER))) {
            if(newestSet == null) {
                workoutRepository.delete(workout);
            } else {
                workout.setStatus(WorkoutStatus.COMPLETED);
                workout.setEndedAt(newestSet);
            }
            workoutRepository.flush();
            return Optional.empty();
        }

        Set<Long> exerciseIds = workout.getExercises().stream()
                .map(WorkoutExercise::getExerciseId)
                .collect(Collectors.toSet());
        return Optional.of(workoutMapper.toWorkoutResponse(
                workout, exerciseService.findSummariesByExerciseId(exerciseIds), Map.of()));
    }

    /**
     * Finishes a running workout owned by the user, its end is the current time.
     * @param userId Id of the requesting user.
     * @param workoutId Id of the workout to finish.
     * @throws WorkoutNotFoundException if no workout with this id is owned by the user
     * @throws WorkoutNotInProgressException if the workout is already finished
     * @throws WorkoutHasNoSetsException if the workout has no logged set, such a workout is discarded instead
     */
    @Transactional
    public void finishWorkout(Long userId, Long workoutId) {
        Workout workout = workoutRepository.getUserWorkoutById(userId, workoutId)
                .orElseThrow(() -> new WorkoutNotFoundException("The workout with the id "+workoutId+" does not exist"));

        if(workout.getStatus() != WorkoutStatus.IN_PROGRESS) {
            throw new WorkoutNotInProgressException("The workout with the id "+workoutId+" is not in progress");
        }
        if(workoutRepository.findNewestSetCreatedAt(workoutId) == null) {
            throw new WorkoutHasNoSetsException("The workout with the id "+workoutId+" does not have any sets");
        }

        workout.setStatus(WorkoutStatus.COMPLETED);
        workout.setEndedAt(Instant.now());
    }

    /**
     * Deletes a workout owned by the user including its exercises and sets, whatever its status. Used to discard a
     * running workout and later to delete one from the history.
     * @param userId Id of the requesting user.
     * @param workoutId Id of the workout to delete.
     * @throws WorkoutNotFoundException if no workout with this id is owned by the user
     */
    @Transactional
    public void deleteWorkout(Long userId, Long workoutId) {
        Workout workout = workoutRepository.getUserWorkoutById(userId, workoutId)
                .orElseThrow(() -> new WorkoutNotFoundException("Workout with the id "+workoutId+" does not exist"));
        workoutRepository.delete(workout);
    }

    /**
     * Starts a workout from an own routine, given directly or through a day of an own program. Every exercise of the
     * routine becomes an entry in the routine's order with its targets copied as snapshots; soft deleted exercises
     * are skipped. The start time is server time.
     * @param userId Id of the requesting user.
     * @param request Either the routine or the program day to start.
     * @return The started workout with its exercises, without sets.
     * @throws WorkoutAlreadyActiveException if the user still has a workout in progress after stale ones were closed
     * @throws RestDayNotStartableException if the program day is a rest day
     * @throws de.phil.fitness.backend.program.exception.ProgramDayNotFoundException if no day with this id is owned
     *         by the user
     * @throws de.phil.fitness.backend.routine.exception.UserRoutineNotFoundException if no routine with this id is
     *         owned by the user
     */
    @Transactional
    public WorkoutResponse startWorkout(Long userId, StartWorkoutRequest request) {
        if (findActiveWorkout(userId).isPresent()) {
            throw new WorkoutAlreadyActiveException("User " + userId + " already has a workout in progress");
        }

        Long routineId = request.routineId();
        if (routineId == null) {
            routineId = programService.findRoutineIdOfUserProgramDay(userId, request.programDayId());
            if (routineId == null) {
                throw new RestDayNotStartableException("Program day " + request.programDayId() + " is a rest day");
            }
        }
        RoutineDetailResponse routine = routineService.findDetailedUserRoutine(userId, routineId);

        Workout workout = new Workout();
        workout.setUserId(userId);
        workout.setRoutineId(routineId);
        workout.setProgramDayId(request.programDayId());
        workout.setName(routine.name());
        workout.setStartedAt(Instant.now());
        workout.setStatus(WorkoutStatus.IN_PROGRESS);

        Map<Long, ExerciseSummary> exercises = new HashMap<>();
        for (RoutineExerciseResponse re : routine.exercises()) {
            if (re.exercise().deleted()) continue;
            WorkoutExercise entry = new WorkoutExercise();
            entry.setExerciseId(re.exercise().id());
            entry.setOrderIndex(workout.getExercises().size());
            entry.setTargetSetsSnapshot(re.targetSets());
            entry.setTargetRepsMinSnapshot(re.targetRepsMin());
            entry.setTargetRepsMaxSnapshot(re.targetRepsMax());
            entry.setTargetDurationSecondsSnapshot(re.targetDurationSeconds());
            entry.setTargetDistanceMetersSnapshot(re.targetDistanceMeters());
            workout.addExercise(entry);
            exercises.put(re.exercise().id(), re.exercise());
        }

        return workoutMapper.toWorkoutResponse(workoutRepository.saveAndFlush(workout), exercises, Map.of());
    }


}
