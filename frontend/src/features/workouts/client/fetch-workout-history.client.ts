import { toApiError } from '@/shared/api/errors/to-api-error';
import type { WorkoutHistory } from '../types/api.types';

export async function fetchWorkoutHistoryPage(page: number): Promise<WorkoutHistory> {
  const response = await fetch(`/api/workouts?page=${page}`);

  if (!response.ok) {
    throw await toApiError(response, 'WORKOUT_HISTORY_FETCH_FAILED');
  }
  return response.json();
}
