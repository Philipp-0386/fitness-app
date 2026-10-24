import { Exercise, ExercisePayload } from '../types/api.types';
import { toApiError } from '@/shared/api/errors/to-api-error';

export async function updateExercise(
  id: string,
  payload: ExercisePayload,
): Promise<Exercise> {
  const response = await fetch(`/api/exercises/${encodeURIComponent(id)}`, {
    method: 'PUT',
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
