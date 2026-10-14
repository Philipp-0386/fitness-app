package de.phil.fitness.backend.exercise.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import de.phil.fitness.backend.exercise.event.ExerciseDeletedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.phil.fitness.backend.exercise.model.Exercise;
import de.phil.fitness.backend.exercise.dto.ExerciseResponse;
import de.phil.fitness.backend.exercise.dto.ExerciseRequest;
import de.phil.fitness.backend.exercise.exception.ExerciseNotFoundException;
import de.phil.fitness.backend.exercise.exception.GlobalExerciseNotEditableException;
import de.phil.fitness.backend.exercise.exception.TrackingTypeLockedException;
import de.phil.fitness.backend.exercise.mapper.ExerciseMapper;
import de.phil.fitness.backend.exercise.repository.ExerciseRepository;
import de.phil.fitness.backend.exercise.dto.ExerciseSummary;
import de.phil.fitness.backend.exercise.dto.UpdateExerciseRequest;

import static java.time.Instant.now;

/**
 * Read and write access to the exercise catalog.
 */
@Service
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final ExerciseMapper exerciseMapper;
    private final ApplicationEventPublisher eventPublisher;

    public ExerciseService(ExerciseRepository exerciseRepository, ExerciseMapper exerciseMapper, ApplicationEventPublisher eventPublisher) {
        this.exerciseRepository = exerciseRepository;
        this.exerciseMapper = exerciseMapper;
        this.eventPublisher = eventPublisher;

    }

    /**
     * Lists every exercise a user may pick from.
     * @param userId id of the user the catalog is assembled for
     * @return global catalog entries merged with that user's own definitions
     */
    @Transactional(readOnly = true)
    public List<ExerciseResponse> findAvailable(Long userId) {
        return exerciseRepository.findAllAvailableToUser(userId)
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

    /**
     * Stores a new exercise owned by the requesting user.
     * @param req the submitted exercise
     * @param userId id of the user the exercise is created for
     * @return the persisted exercise, including its generated id
     */
    @Transactional
    public ExerciseResponse createNewExercise(ExerciseRequest req, Long userId) {
        Exercise newExercise = exerciseMapper.mapRequestToExerciseEntity(req, userId);
        return exerciseMapper.toResponse(exerciseRepository.save(newExercise));
    }

    /**
     * Resolves exercises being referenced by other slices, like "routine". No ownership validation here. That happens in other slices.
     * @param exerciseIds Ids to resolve.
     * @return Summaries keyed by their Ids.
     */
    @Transactional(readOnly = true)
    public Map<Long, ExerciseSummary> findSummariesByExerciseId(Collection<Long> exerciseIds) {
        return exerciseRepository.findAllById(exerciseIds)
                .stream()
                .collect(Collectors.toMap(Exercise::getId, exerciseMapper::toSummary));
    }

    /**
     * Ensures every given exercise may be referenced by the user, returns silently if so.
     * @param userId Id of the user referencing the exercises.
     * @param exerciseIds Ids of the referenced exercises.
     * @throws ExerciseNotFoundException for the first id that is not available to the user, whether because it does
     *         not exist, is soft deleted or belongs to someone else
     */
    @Transactional(readOnly = true)
    public void idAvailabilityCheck(Long userId, Set<Long> exerciseIds) {
        if (exerciseIds.isEmpty()) return;

        Set<Long> available = exerciseRepository.findAvailableIds(userId, exerciseIds);
        exerciseIds.stream()
                .filter(id -> !available.contains(id))
                .findFirst()
                .ifPresent(id -> {
                    throw new ExerciseNotFoundException("The exercise with id=" + id + " can not be found");
                });
    }

    /**
     * Updates an exercise owned by the requesting user.
     * @param userId id of the requesting user
     * @param exerciseId id of the exercise to update
     * @param req the complete new state of the exercise
     * @return the updated exercise
     * @throws ExerciseNotFoundException if the exercise does not exist, is soft deleted or belongs to someone else
     * @throws GlobalExerciseNotEditableException if the exercise is part of the global catalog, and therefore not editable
     * @throws TrackingTypeLockedException if the tracking type changes while the exercise is referenced
     */
    @Transactional
    public ExerciseResponse updateExercise(Long userId, Long exerciseId, UpdateExerciseRequest req) {
        Exercise exercise = exerciseRepository.findAvailableById(userId, exerciseId)
                .orElseThrow(() -> new ExerciseNotFoundException("The exercise with id=" + exerciseId + " can not be found"));

        if (exercise.getOwnerUserId() == null) {
            throw new GlobalExerciseNotEditableException("User " + userId + " tried to change global exercise " + exerciseId);
        }
        if (exercise.getTrackingType() != req.trackingType() && exerciseRepository.isReferenced(exerciseId)) {
            throw new TrackingTypeLockedException("Tracking type of exercise " + exerciseId + " is locked, it is referenced");
        }

        exerciseMapper.applyUpdate(exercise, req);
        return exerciseMapper.toResponse(exerciseRepository.saveAndFlush(exercise));
    }

    /**
     * Deletes an exercise owned by the requesting user.
     *
     * Important note: Deletes the exercise also from all routines which include said exercise, NOT workouts though.
     * @param userId id of the requesting user
     * @param exerciseId id of the exercise to delete
     * @throws ExerciseNotFoundException if the exercise does not exist, or is already soft deleted or belongs to someone else
     * @throws GlobalExerciseNotEditableException if the exercise is part of the global catalog, and therefore not editable
     */
    @Transactional
    public void deleteUserExercise(Long userId, Long exerciseId) {
        Exercise exercise = exerciseRepository.findAvailableById(userId, exerciseId)
                .orElseThrow(() -> new ExerciseNotFoundException("The exercise with id=" + exerciseId + " can not be found"));

        if (exercise.getOwnerUserId() == null) {
            throw new GlobalExerciseNotEditableException("User " + userId + " tried to delete global exercise " + exerciseId);
        }

        eventPublisher.publishEvent(new ExerciseDeletedEvent(userId, exerciseId));
        exercise.setDeletedAt(now());
        exerciseRepository.save(exercise);
    }
}
