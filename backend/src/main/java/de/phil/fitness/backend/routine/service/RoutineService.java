package de.phil.fitness.backend.routine.service;

import java.util.List;
import java.util.Optional;

import de.phil.fitness.backend.routine.dto.RoutineResponse;
import de.phil.fitness.backend.routine.exception.PresetRoutineNotFoundException;
import de.phil.fitness.backend.routine.exception.UserRoutineNotFoundException;
import de.phil.fitness.backend.routine.model.Routine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.phil.fitness.backend.routine.mapper.RoutineMapper;
import de.phil.fitness.backend.routine.repository.RoutineRepository;

@Service
public class RoutineService {
    private final RoutineRepository routineRepository;
    private final RoutineMapper routineMapper;

    public RoutineService(RoutineRepository routineRepository, RoutineMapper routineMapper) {
        this.routineRepository = routineRepository;
        this.routineMapper = routineMapper;
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
}
