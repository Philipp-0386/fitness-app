package de.phil.fitness.backend.routine.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import de.phil.fitness.backend.exercise.dto.ExerciseSummary;
import de.phil.fitness.backend.exercise.model.TrackingType;
import de.phil.fitness.backend.exercise.service.ExerciseService;
import de.phil.fitness.backend.routine.dto.CreateRoutineRequest;
import de.phil.fitness.backend.routine.dto.RoutineDetailResponse;
import de.phil.fitness.backend.routine.dto.RoutineExerciseRequest;
import de.phil.fitness.backend.routine.dto.RoutineResponse;
import de.phil.fitness.backend.routine.exception.PresetRoutineNotFoundException;
import de.phil.fitness.backend.routine.exception.RoutineTargetMismatchException;
import de.phil.fitness.backend.routine.exception.UserRoutineNotFoundException;
import de.phil.fitness.backend.routine.model.Routine;
import de.phil.fitness.backend.routine.model.RoutineExercise;
import de.phil.fitness.backend.routine.dto.UpdateRoutineRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.phil.fitness.backend.routine.mapper.RoutineMapper;
import de.phil.fitness.backend.routine.repository.RoutineRepository;

@Service
public class RoutineService {
    private final RoutineRepository routineRepository;
    private final RoutineMapper routineMapper;
    private final ExerciseService exerciseService;

    public RoutineService(RoutineRepository routineRepository, RoutineMapper routineMapper, ExerciseService exerciseService) {
        this.routineRepository = routineRepository;
        this.routineMapper = routineMapper;
        this.exerciseService = exerciseService;
    }

    /**
     * Lists all user-made routines.
     * @param userId Id of the requesting user.
     * @return List of user-owned routines.
     */
    @Transactional(readOnly = true)
    public List<RoutineResponse> findUserRoutines(Long userId) {
        return routineRepository.getAllUserRoutinesByUserId(userId)
                .stream()
                .map(routineMapper::toRoutineResponse)
                .toList();
    }

    /**
     * Retrieves single user-routine by the user's and routine's id.
     * @param userId Id of the requesting user.
     * @param routineId Id of the searched routine.
     * @return Singular user-owned routine.
     * @throws UserRoutineNotFoundException if no routine with this id is owned by the user
     */
    @Transactional(readOnly = true)
    public RoutineResponse findSpecificUserRoutine(Long userId, Long routineId) {
        return routineRepository.getUserRoutineById(userId, routineId)
                .map(routineMapper::toRoutineResponse)
                .orElseThrow(() -> new UserRoutineNotFoundException("The user-routine with id " + routineId + " does not exist"));
    }

    /**
     * Retrieves single user-owned routine including the exercises it includes.
     * @param userId Id of the requesting user.
     * @param routineId Id of the searched routine.
     * @return Singular user-owned routine with its exercises.
     * @throws UserRoutineNotFoundException if no routine with this id is owned by the user
     */
    @Transactional(readOnly = true)
    public RoutineDetailResponse findDetailedUserRoutine(Long userId, Long routineId) {
        Routine routine = routineRepository.getUserRoutineDetailedById(userId, routineId)
                .orElseThrow(() -> new UserRoutineNotFoundException("The user-routine with id " + routineId + " does not exist"));
        Set<Long> exerciseIds = routine.getExercises().stream()
                .map(RoutineExercise::getExerciseId)
                .collect(Collectors.toSet());

        return routineMapper.toRoutineDetailResponse(routine, exerciseService.findSummariesByExerciseId(exerciseIds));
    }

    /**
     * Deletes a user-owned routine including its exercises.
     * @param userId Id of the requesting user.
     * @param routineId Id of the routine to delete.
     * @throws UserRoutineNotFoundException if no routine with this id is owned by the user
     */
    @Transactional
    public void deleteUserRoutine(Long userId, Long routineId) {
        Routine routine = routineRepository.getUserRoutineById(userId, routineId)
                .orElseThrow(() -> new UserRoutineNotFoundException("The user-routine with id " + routineId + " does not exist"));
        routineRepository.delete(routine);
    }

    /**
     * Updates a user-owned routine including its exercises.
     * @param userId Id of the requesting user.
     * @param routineId Id of the routine to update.
     * @param request Request containing information to update the routine with.
     * @return The updated routine with its exercises.
     * @throws UserRoutineNotFoundException if no routine with this id is owned by the user
     * @throws de.phil.fitness.backend.exercise.exception.ExerciseNotFoundException if a referenced exercise is not
     *         available to the user
     * @throws RoutineTargetMismatchException if a target is set that the tracking type of its exercise does not use
     */
    @Transactional
    public RoutineDetailResponse updateUserRoutine(Long userId, Long routineId, UpdateRoutineRequest request) {
        Routine routine = routineRepository.getUserRoutineDetailedById(userId, routineId)
                .orElseThrow(() -> new UserRoutineNotFoundException("The user-routine with id " + routineId + " does not exist"));

        Set<Long> exerciseIds;
        if (request.exercises() == null) {
            exerciseIds = Set.of();
        } else {
            exerciseIds = request.exercises().stream()
                    .map(RoutineExerciseRequest::exerciseId)
                    .collect(Collectors.toSet());
        }

        exerciseService.idAvailabilityCheck(userId, exerciseIds);
        Map<Long, ExerciseSummary> exercises = exerciseService.findSummariesByExerciseId(exerciseIds);
        routineMapper.applyUpdateOfUserRoutine(routine, request);

        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (RoutineExercise re : routine.getExercises()) {
            TrackingType type = exercises.get(re.getExerciseId()).trackingType();
            String path = "exercises[" + re.getOrderIndex() + "].";
            String message = "not used by tracking type " + type;
            if (re.getTargetRepsMin() != null && !type.holdsReps()) fieldErrors.put(path + "targetRepsMin", message);
            if (re.getTargetRepsMax() != null && !type.holdsReps()) fieldErrors.put(path + "targetRepsMax", message);
            if (re.getTargetDurationSeconds() != null && !type.holdsDuration()) fieldErrors.put(path + "targetDurationSeconds", message);
            if (re.getTargetDistanceMeters() != null && !type.holdsDistance()) fieldErrors.put(path + "targetDistanceMeters", message);
        }
        if (!fieldErrors.isEmpty()) {
            throw new RoutineTargetMismatchException("Targets of routine " + routineId + " do not match the tracking types: " + fieldErrors.keySet(), fieldErrors);
        }

        return routineMapper.toRoutineDetailResponse(routineRepository.saveAndFlush(routine), exercises);
    }

    /**
     * Lists all preset-routines defined by the system as defaults to access for all users.
     * @return List of all preset-routines.
     */
    @Transactional(readOnly = true)
    public List<RoutineResponse> findPresetRoutines() {
        return routineRepository.getAllPresetRoutines()
                .stream()
                .map(routineMapper::toRoutineResponse)
                .toList();
    }

    /**
     * Retrieves single preset-routine by its Id.
     * @param routineId Id of the searched routine.
     * @return Singular preset-routine.
     * @throws PresetRoutineNotFoundException if no preset-routine with this id exists
     */
    @Transactional(readOnly = true)
    public RoutineResponse findSpecificPresetRoutine(Long routineId) {
        return routineRepository.getPresetRoutineById(routineId)
                .map(routineMapper::toRoutineResponse)
                .orElseThrow(() -> new PresetRoutineNotFoundException("The preset-routine with id " + routineId + " does not exist"));
    }

    /**
     * Retrieves single preset-routine including the exercises it includes.
     * @param routineId Id of the searched routine.
     * @return Singular preset-routine with its exercises.
     * @throws PresetRoutineNotFoundException if no preset-routine with this id exists
     */
    @Transactional(readOnly = true)
    public RoutineDetailResponse findDetailedPresetRoutine(Long routineId) {
        Routine routine = routineRepository.getPresetRoutineDetailedById(routineId)
                .orElseThrow(() -> new PresetRoutineNotFoundException("The preset-routine with id " + routineId + " does not exist"));
        Set<Long> exerciseIds = routine.getExercises().stream()
                .map(RoutineExercise::getExerciseId)
                .collect(Collectors.toSet());

        return routineMapper.toRoutineDetailResponse(routine, exerciseService.findSummariesByExerciseId(exerciseIds));
    }

    /**
     * Creates a routine owned by the requesting user, including its exercises if any are given.
     * @param createRoutineRequest The submitted routine, the order of its exercises is taken from the list.
     * @param userId Id of the user the routine is created for.
     * @return The created routine with its exercises.
     * @throws de.phil.fitness.backend.exercise.exception.ExerciseNotFoundException if a referenced exercise is not
     *         available to the user
     * @throws RoutineTargetMismatchException if a target is set that the tracking type of its exercise does not use
     */
    @Transactional
    public RoutineDetailResponse createNewUserRoutine(CreateRoutineRequest createRoutineRequest, Long userId) {
        Routine routine = routineMapper.toRoutineEntity(createRoutineRequest, userId);

        Set<Long> exerciseIds = routine.getExercises().stream()
                .map(RoutineExercise::getExerciseId)
                .collect(Collectors.toSet());

        exerciseService.idAvailabilityCheck(userId, exerciseIds);
        Map<Long, ExerciseSummary> exercises = exerciseService.findSummariesByExerciseId(exerciseIds);

        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (RoutineExercise re : routine.getExercises()) {
            TrackingType type = exercises.get(re.getExerciseId()).trackingType();
            String path = "exercises[" + re.getOrderIndex() + "].";
            String message = "not used by tracking type " + type;
            if (re.getTargetRepsMin() != null && !type.holdsReps()) fieldErrors.put(path + "targetRepsMin", message);
            if (re.getTargetRepsMax() != null && !type.holdsReps()) fieldErrors.put(path + "targetRepsMax", message);
            if (re.getTargetDurationSeconds() != null && !type.holdsDuration()) fieldErrors.put(path + "targetDurationSeconds", message);
            if (re.getTargetDistanceMeters() != null && !type.holdsDistance()) fieldErrors.put(path + "targetDistanceMeters", message);
        }
        if (!fieldErrors.isEmpty()) {
            throw new RoutineTargetMismatchException("Targets of a new routine do not match the tracking types: " + fieldErrors.keySet(), fieldErrors);
        }

        return routineMapper.toRoutineDetailResponse(routineRepository.save(routine), exercises);
    }
}
