import 'server-only';

import backendFetch from '@/shared/api/backend';
import { toApiError } from '@/shared/api/errors/to-api-error';
import { Workout, WorkoutHistory } from '../types/api.types';

/**
 * Reads the workout the current user has in progress. The backend closes a workout without activity for four
 * hours on the way.
 * @returns Returns null if the user has no workout in progress.
 */
export async function fetchActiveWorkout(): Promise<Workout | null> {
  const response = await backendFetch('/backend/workouts/active');

  if (response.status === 204) {
    return null;
  }
  if (!response.ok) {
    throw await toApiError(response, 'ACTIVE_WORKOUT_FETCH_FAILED');
  }

  return response.json();
}

/**
 * Reads a single workout by its ID including exercises and sets
 * @param workoutId Id of the workout to read
 * @throws ApiError with 404 if the workout does not exist or is not accessible by the current user.
 * @returns Returns the workout with the given ID
 */
export async function fetchWorkoutById(workoutId: string): Promise<Workout> {
  const response = await backendFetch(
    `/backend/workouts/${encodeURIComponent(workoutId)}`,
  );

  if (!response.ok) {
    throw await toApiError(response, 'WORKOUT_FETCH_FAILED');
  }

  return response.json();
}

/**
 * Reads the workout history of the current user.
 * @param page Number of page to read. Default 0
 * @returns Workout history of the current user.
 */
export async function fetchWorkoutHistory(page = 0): Promise<WorkoutHistory> {
  const response = await backendFetch(`/backend/workouts?page=${page}`);

  if (!response.ok) {
    throw await toApiError(response, 'WORKOUT_HISTORY_FETCH_FAILED');
  }

  return response.json();
}
