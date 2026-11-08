import { StartWorkoutPayload, Workout } from '../types/api.types';
import { toApiError } from '@/shared/api/errors/to-api-error';

export async function startWorkout(payload: StartWorkoutPayload): Promise<Workout> {
  const response = await fetch('/api/workouts', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  });

  if (!response.ok) {
    throw await toApiError(response);
  }

  return response.json();
}
