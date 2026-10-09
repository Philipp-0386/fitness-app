package de.phil.fitness.backend.routine.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.phil.fitness.backend.exercise.service.ExerciseService;
import de.phil.fitness.backend.routine.dto.CreateRoutineRequest;
import de.phil.fitness.backend.routine.dto.RoutineDetailResponse;
import de.phil.fitness.backend.routine.dto.RoutineExerciseRequest;
import de.phil.fitness.backend.routine.dto.RoutineResponse;
import de.phil.fitness.backend.routine.exception.PresetRoutineNotFoundException;
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
     * @throws de.phil.fitness.backend.exercise.exception.ExerciseNotFoundException if a referenced exercise is not
     *         available to the user
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
        routineMapper.applyUpdateOfUserRoutine(routine, request);
        return routineMapper.toRoutineDetailResponse(routineRepository.saveAndFlush(routine), exerciseService.findSummariesByExerciseId(exerciseIds));
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
     */
    @Transactional
    public RoutineDetailResponse createNewUserRoutine(CreateRoutineRequest createRoutineRequest, Long userId) {
        Routine routine = routineMapper.toRoutineEntity(createRoutineRequest, userId);

        Set<Long> exerciseIds = routine.getExercises().stream()
                .map(RoutineExercise::getExerciseId)
                .collect(Collectors.toSet());

        exerciseService.idAvailabilityCheck(userId, exerciseIds);
        return routineMapper.toRoutineDetailResponse(routineRepository.save(routine), exerciseService.findSummariesByExerciseId(exerciseIds));
    }
}
