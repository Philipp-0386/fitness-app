package de.phil.fitness.backend.exercise.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.phil.fitness.backend.exercise.dto.ExerciseResponse;
import de.phil.fitness.backend.exercise.exception.ExerciseNotFoundException;
import de.phil.fitness.backend.exercise.mapper.ExerciseMapper;
import de.phil.fitness.backend.exercise.repository.ExerciseRepository;

/**
 * Read access to the exercise catalog.
 */
@Service
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final ExerciseMapper exerciseMapper;

    public ExerciseService(ExerciseRepository exerciseRepository,
                           ExerciseMapper exerciseMapper) {
        this.exerciseRepository = exerciseRepository;
        this.exerciseMapper = exerciseMapper;
    }

    /**
     * Lists every exercise a user may pick from.
     * @param userId id of the user the catalog is assembled for
     * @return global catalog entries merged with that user's own definitions
     */
    @Transactional(readOnly = true)
    public List<ExerciseResponse> findAvailable(Long userId) {
        return exerciseRepository.findAvailableTo(userId)
                .stream()
                .map(exerciseMapper::toResponse)
                .toList();
    }

    /**
     * Reads a single exercise, provided the requesting user may see it.
     * @param userId id of the user requesting the exercise
     * @param exerciseId id of the requested exercise
     * @return the exercise, if it is global or owned by that user and not soft deleted
     * @throws ExerciseNotFoundException if no exercise is available to the user, whether because the id does not
     *         exist, is soft deleted or belongs to someone else
     */
    @Transactional(readOnly = true)
    public ExerciseResponse findExerciseById(Long userId, Long exerciseId) {
        return exerciseRepository.findAvailableById(userId, exerciseId)
                .map(exerciseMapper::toResponse)
                .orElseThrow(() -> new ExerciseNotFoundException("The exercise with id=" + exerciseId + " can not be found"));
    }
}
