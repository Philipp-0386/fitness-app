import 'server-only';

import backendFetch from '@/shared/api/backend';
import { toApiError } from '@/shared/api/errors/to-api-error';
import { Workout } from '../types/api.types';

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
