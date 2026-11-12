package de.phil.fitness.backend.workout.service;

import de.phil.fitness.backend.exercise.dto.ExerciseSummary;
import de.phil.fitness.backend.exercise.model.TrackingType;
import de.phil.fitness.backend.exercise.service.ExerciseService;
import de.phil.fitness.backend.program.service.ProgramService;
import de.phil.fitness.backend.routine.dto.RoutineDetailResponse;
import de.phil.fitness.backend.routine.dto.RoutineExerciseResponse;
import de.phil.fitness.backend.routine.service.RoutineService;
import de.phil.fitness.backend.workout.dto.*;
import de.phil.fitness.backend.workout.exception.*;
import de.phil.fitness.backend.workout.mapper.WorkoutMapper;
import de.phil.fitness.backend.workout.model.*;
import de.phil.fitness.backend.workout.repository.LastTimeEntry;
import de.phil.fitness.backend.workout.repository.WorkoutEntryCounts;
import de.phil.fitness.backend.workout.repository.WorkoutRepository;
import de.phil.fitness.backend.workout.repository.WorkoutSetRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WorkoutService {
    private final WorkoutRepository workoutRepository;
    private final WorkoutMapper workoutMapper;

    private final ExerciseService exerciseService;
    private final RoutineService routineService;
    private final ProgramService programService;

    private static final Duration STALE_AFTER = Duration.ofHours(4); //auto-close var
    private final WorkoutSetRepository workoutSetRepository;

    public WorkoutService(WorkoutRepository workoutRepository, WorkoutMapper workoutMapper,
                          ExerciseService exerciseService, RoutineService routineService, ProgramService programService, WorkoutSetRepository workoutSetRepository) {
        this.workoutRepository = workoutRepository;
        this.workoutMapper = workoutMapper;
        this.exerciseService = exerciseService;
        this.routineService = routineService;
        this.programService = programService;
        this.workoutSetRepository = workoutSetRepository;
    }

    @Transactional(readOnly = true)
    public WorkoutResponse findWorkout(Long userId, Long workoutId) {
        Workout workout = workoutRepository.getUserWorkoutById(userId, workoutId)
                .orElseThrow( () -> new WorkoutNotFoundException("The workout with the id "+workoutId+" does not exist"));

        Set<Long> exerciseIds = workout.getExercises().stream()
                .map(WorkoutExercise::getExerciseId)
                .collect(Collectors.toSet());

        return workoutMapper.toWorkoutResponse(workout, exerciseService.findSummariesByExerciseId(exerciseIds), Map.of());
    }

    /**
     * Retrieves a page of the user's finished workouts, newest first, each with its exercise names and number of sets.
     * Three queries per page, however many workouts it holds.
     * @param userId Id of the requesting user.
     * @param pageable Page number and size.
     * @return The workouts of the page and whether older ones follow.
     */
    @Transactional(readOnly = true)
    public WorkoutHistoryResponse findWorkoutHistory(Long userId, Pageable pageable) {
        Slice<Workout> page = workoutRepository.findCompletedUserWorkouts(userId, pageable);
        if (page.isEmpty()) {
            return new WorkoutHistoryResponse(List.of(), false);
        }

        List<WorkoutEntryCounts> entries = workoutRepository.countSetsPerEntry(
                page.stream().map(Workout::getId).toList());
        Map<Long, ExerciseSummary> exercises = exerciseService.findSummariesByExerciseId(
                entries.stream().map(WorkoutEntryCounts::getExerciseId).collect(Collectors.toSet()));
        Map<Long, List<WorkoutEntryCounts>> byWorkout = entries.stream()
                .collect(Collectors.groupingBy(WorkoutEntryCounts::getWorkoutId));

        return new WorkoutHistoryResponse(
                page.stream()
                        .map(w -> workoutMapper.toWorkoutListItemResponse(
                                w, byWorkout.getOrDefault(w.getId(), List.of()), exercises))
                        .toList(),
                page.hasNext());
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
        Map<Long, LastTimeResponse> lastTimes = Map.of();
        if (!exerciseIds.isEmpty()) {
            List<LastTimeEntry> entries = workoutRepository.findLastTimeEntries(userId, workout.getId(), exerciseIds);
            List<WorkoutSet> sets = entries.isEmpty() ? List.of()
                    : workoutSetRepository.getSetsOfEntries(entries.stream().map(LastTimeEntry::getEntryId).toList());
            lastTimes = workoutMapper.toLastTimeResponses(entries, sets);
        }
        return Optional.of(workoutMapper.toWorkoutResponse(
                workout, exerciseService.findSummariesByExerciseId(exerciseIds), lastTimes));
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

        Workout saved = workoutRepository.saveAndFlush(workout);

        Map<Long, LastTimeResponse> lastTimes = Map.of();
        if (!exercises.isEmpty()) {
            List<LastTimeEntry> entries = workoutRepository.findLastTimeEntries(userId, saved.getId(), exercises.keySet());
            List<WorkoutSet> sets = entries.isEmpty() ? List.of()
                    : workoutSetRepository.getSetsOfEntries(entries.stream().map(LastTimeEntry::getEntryId).toList());
            lastTimes = workoutMapper.toLastTimeResponses(entries, sets);
        }
        return workoutMapper.toWorkoutResponse(saved, exercises, lastTimes);
    }

    /**
     * Logs or replaces a set of a workout owned by the user, identified by the id the client generated for it, so a
     * retried request changes the same set instead of creating a second one. The workout's status is not checked,
     * finished workouts can be corrected through the same method.
     * @param userId Id of the requesting user.
     * @param workoutId Id of the workout the set belongs to.
     * @param clientId Id the client generated for the set.
     * @param req The complete new state of the set; which values are required depends on the exercise's tracking type.
     * @return The saved set.
     * @throws WorkoutNotFoundException if no workout with this id is owned by the user, or the client id belongs to a
     *         set of another workout
     * @throws WorkoutExerciseNotFoundException if a new set refers to an exercise entry that is not part of the workout
     * @throws InvalidSetException if an existing set would move to another exercise entry, or the values do not match
     *         the tracking type
     * @throws SetNumberTakenException if another set of the exercise entry already has this set number
     */
    @Transactional
    public WorkoutSetResponse saveSet(Long userId, Long workoutId, UUID clientId, WorkoutSetRequest req) {
        Workout workout = workoutRepository.getUserWorkoutById(userId, workoutId)
                .orElseThrow(() -> new WorkoutNotFoundException("Workout with the id "+workoutId+" does not exist"));

        Optional<WorkoutSet> existing = workoutSetRepository.findByClientId(clientId);
        WorkoutExercise entry;
        if (existing.isPresent()) {
            entry = existing.get().getWorkoutExercise();
            if (!entry.getWorkout().getId().equals(workoutId)) {
                throw new WorkoutNotFoundException("The set " + clientId + " does not belong to workout " + workoutId);
            }
            if (!entry.getId().equals(req.workoutExerciseId())) {
                throw new InvalidSetException("The set " + clientId + " cannot move to workout exercise " + req.workoutExerciseId(),
                        Map.of("workoutExerciseId", "cannot change on an existing set"));
            }
        } else {
            entry = workout.getExercises().stream()
                    .filter(we -> we.getId().equals(req.workoutExerciseId()))
                    .findFirst()
                    .orElseThrow(() -> new WorkoutExerciseNotFoundException("Workout exercise " + req.workoutExerciseId() + " does not belong to workout " + workoutId));
        }

        boolean numberTaken = entry.getSets().stream()
                .anyMatch(s -> s.getSetNumber().equals(req.setNumber()) && !s.getClientId().equals(clientId));
        if (numberTaken) {
            throw new SetNumberTakenException("Workout exercise " + entry.getId() + " already has a set with number " + req.setNumber());
        }

        TrackingType type = exerciseService.findSummariesByExerciseId(Set.of(entry.getExerciseId()))
                .get(entry.getExerciseId()).trackingType();
        boolean weightRequired = type == TrackingType.WEIGHT_REPS || type == TrackingType.WEIGHT_DISTANCE;
        boolean weightForbidden = type == TrackingType.DISTANCE_DURATION;

        String required = "required for tracking type " + type;
        String notUsed = "not used by tracking type " + type;
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        if (type.holdsReps() && req.reps() == null) fieldErrors.put("reps", required);
        if (!type.holdsReps() && req.reps() != null) fieldErrors.put("reps", notUsed);
        if (weightRequired && req.weightKg() == null) fieldErrors.put("weightKg", required);
        if (weightForbidden && req.weightKg() != null) fieldErrors.put("weightKg", notUsed);
        if (type.holdsDuration() && req.durationSeconds() == null) fieldErrors.put("durationSeconds", required);
        if (!type.holdsDuration() && req.durationSeconds() != null) fieldErrors.put("durationSeconds", notUsed);
        if (type.holdsDistance() && req.distanceMeters() == null) fieldErrors.put("distanceMeters", required);
        if (!type.holdsDistance() && req.distanceMeters() != null) fieldErrors.put("distanceMeters", notUsed);
        if (!fieldErrors.isEmpty()) {
            throw new InvalidSetException("Set " + clientId + " does not match tracking type " + type + ": " + fieldErrors.keySet(), fieldErrors);
        }

        WorkoutSet set = existing.orElseGet(() -> {
            WorkoutSet created = new WorkoutSet();
            created.setClientId(clientId);
            entry.addSet(created);
            return created;
        });
        set.setSetNumber(req.setNumber());
        set.setSetType(req.setType() != null ? req.setType() : SetType.WORKING);
        set.setRpe(req.rpe());
        set.setReps(req.reps());
        set.setWeightKg(req.weightKg());
        set.setDurationSeconds(req.durationSeconds());
        set.setDistanceMeters(req.distanceMeters());

        return workoutMapper.toWorkoutSetResponse(workoutSetRepository.saveAndFlush(set));
    }

    /**
     * Deletes a set of a workout owned by the user. Deleting a set that does not exist, or no longer exists, succeeds
     * silently, so a retried request does not fail.
     * @param userId Id of the requesting user.
     * @param workoutId Id of the workout the set belongs to.
     * @param clientId Id the client generated for the set.
     * @throws WorkoutNotFoundException if no workout with this id is owned by the user
     */
    @Transactional
    public void deleteSet(Long userId, Long workoutId, UUID clientId) {
        workoutRepository.getUserWorkoutById(userId, workoutId)
                .orElseThrow(() -> new WorkoutNotFoundException("Workout with the id " + workoutId + " does not exist"));

        workoutSetRepository.findByClientId(clientId)
                .filter(set -> set.getWorkoutExercise().getWorkout().getId().equals(workoutId))
                .ifPresent(set -> set.getWorkoutExercise().getSets().remove(set));
    }

    /**
     * Appends an exercise to a workout owned by the user, without targets.
     * @param userId Id of the requesting user.
     * @param workoutId Id of the workout.
     * @param request The exercise to add.
     * @return The new entry with the exercise's last time.
     * @throws WorkoutNotFoundException if no workout with this id is owned by the user
     * @throws de.phil.fitness.backend.exercise.exception.ExerciseNotFoundException if the exercise is not available
     *         to the user or deleted
     * @throws ExerciseAlreadyInWorkoutException if another entry of the workout already has this exercise
     */
    @Transactional
    public WorkoutExerciseResponse addExercise(Long userId, Long workoutId, WorkoutExerciseRequest request) {
        Workout workout = workoutRepository.getUserWorkoutById(userId, workoutId)
                .orElseThrow(() -> new WorkoutNotFoundException("Workout with the id " + workoutId + " does not exist"));
        Long exerciseId = request.exerciseId();
        exerciseService.idAvailabilityCheck(userId, Set.of(exerciseId));
        if (workout.getExercises().stream().anyMatch(we -> we.getExerciseId().equals(exerciseId))) {
            throw new ExerciseAlreadyInWorkoutException("Exercise " + exerciseId + " is already part of workout " + workoutId);
        }

        WorkoutExercise entry = new WorkoutExercise();
        entry.setExerciseId(exerciseId);
        entry.setOrderIndex(workout.getExercises().stream()
                .mapToInt(WorkoutExercise::getOrderIndex)
                .max()
                .orElse(-1) + 1);
        workout.addExercise(entry);
        workoutRepository.flush();

        List<LastTimeEntry> entries = workoutRepository.findLastTimeEntries(userId, workoutId, Set.of(exerciseId));
        List<WorkoutSet> sets = entries.isEmpty() ? List.of()
                : workoutSetRepository.getSetsOfEntries(entries.stream().map(LastTimeEntry::getEntryId).toList());
        return workoutMapper.toWorkoutExerciseResponse(entry, exerciseService.findSummariesByExerciseId(Set.of(exerciseId)),
                workoutMapper.toLastTimeResponses(entries, sets));
    }

    /**
     * Replaces the exercise of an entry without logged sets. The position stays, the target snapshots are kept as far
     * as the new exercise's tracking type uses them.
     * @param userId Id of the requesting user.
     * @param workoutId Id of the workout.
     * @param entryId Id of the entry to swap.
     * @param request The new exercise.
     * @return The changed entry with the new exercise's last time.
     * @throws WorkoutNotFoundException if no workout with this id is owned by the user
     * @throws WorkoutExerciseNotFoundException if the entry is not part of the workout
     * @throws WorkoutExerciseHasSetsException if the entry has logged sets
     * @throws de.phil.fitness.backend.exercise.exception.ExerciseNotFoundException if the exercise is not available
     *         to the user or deleted
     * @throws ExerciseAlreadyInWorkoutException if another entry of the workout already has this exercise
     */
    @Transactional
    public WorkoutExerciseResponse swapExercise(Long userId, Long workoutId, Long entryId, WorkoutExerciseRequest request) {
        Workout workout = workoutRepository.getUserWorkoutById(userId, workoutId)
                .orElseThrow(() -> new WorkoutNotFoundException("Workout with the id " + workoutId + " does not exist"));
        WorkoutExercise entry = workout.getExercises().stream()
                .filter(we -> we.getId().equals(entryId))
                .findFirst()
                .orElseThrow(() -> new WorkoutExerciseNotFoundException("Workout exercise " + entryId + " does not belong to workout " + workoutId));
        if (!entry.getSets().isEmpty()) {
            throw new WorkoutExerciseHasSetsException("Workout exercise " + entryId + " has logged sets");
        }
        Long exerciseId = request.exerciseId();
        exerciseService.idAvailabilityCheck(userId, Set.of(exerciseId));
        if (workout.getExercises().stream().anyMatch(we -> we != entry && we.getExerciseId().equals(exerciseId))) {
            throw new ExerciseAlreadyInWorkoutException("Exercise " + exerciseId + " is already part of workout " + workoutId);
        }

        Map<Long, ExerciseSummary> summaries = exerciseService.findSummariesByExerciseId(Set.of(exerciseId));
        TrackingType type = summaries.get(exerciseId).trackingType();
        entry.setExerciseId(exerciseId);
        if (!type.holdsReps()) {
            entry.setTargetRepsMinSnapshot(null);
            entry.setTargetRepsMaxSnapshot(null);
        }
        if (!type.holdsDuration()) entry.setTargetDurationSecondsSnapshot(null);
        if (!type.holdsDistance()) entry.setTargetDistanceMetersSnapshot(null);
        workoutRepository.saveAndFlush(workout);

        List<LastTimeEntry> entries = workoutRepository.findLastTimeEntries(userId, workoutId, Set.of(exerciseId));
        List<WorkoutSet> sets = entries.isEmpty() ? List.of()
                : workoutSetRepository.getSetsOfEntries(entries.stream().map(LastTimeEntry::getEntryId).toList());
        return workoutMapper.toWorkoutExerciseResponse(entry, summaries, workoutMapper.toLastTimeResponses(entries, sets));
    }

    /**
     * Removes an entry including its sets from a workout owned by the user. The other entries keep their order index.
     * @param userId Id of the requesting user.
     * @param workoutId Id of the workout.
     * @param entryId Id of the entry to remove.
     * @throws WorkoutNotFoundException if no workout with this id is owned by the user
     * @throws WorkoutExerciseNotFoundException if the entry is not part of the workout
     */
    @Transactional
    public void removeExercise(Long userId, Long workoutId, Long entryId) {
        Workout workout = workoutRepository.getUserWorkoutById(userId, workoutId)
                .orElseThrow(() -> new WorkoutNotFoundException("Workout with the id " + workoutId + " does not exist"));
        WorkoutExercise entry = workout.getExercises().stream()
                .filter(we -> we.getId().equals(entryId))
                .findFirst()
                .orElseThrow(() -> new WorkoutExerciseNotFoundException("Workout exercise " + entryId + " does not belong to workout " + workoutId));
        workout.getExercises().remove(entry);
    }

    /**
     * Sets the complete new order of a workout's entries; entries with sets move too.
     * @param userId Id of the requesting user.
     * @param workoutId Id of the workout.
     * @param request Every entry id of the workout exactly once, in the new order.
     * @throws WorkoutNotFoundException if no workout with this id is owned by the user
     * @throws InvalidExerciseOrderException if an entry is missing, twice or not part of the workout
     */
    @Transactional
    public void reorderExercises(Long userId, Long workoutId, WorkoutExerciseOrderRequest request) {
        Workout workout = workoutRepository.getUserWorkoutById(userId, workoutId)
                .orElseThrow(() -> new WorkoutNotFoundException("Workout with the id " + workoutId + " does not exist"));
        List<Long> order = request.entryIds();
        Map<Long, WorkoutExercise> entries = workout.getExercises().stream()
                .collect(Collectors.toMap(WorkoutExercise::getId, we -> we));
        if (order.size() != entries.size() || !new HashSet<>(order).equals(entries.keySet())) {
            throw new InvalidExerciseOrderException("Order " + order + " does not match the entries " + entries.keySet() + " of workout " + workoutId);
        }

        for (int i = 0; i < order.size(); i++) {
            entries.get(order.get(i)).setOrderIndex(i);
        }
        workoutRepository.saveAndFlush(workout);
    }
}
