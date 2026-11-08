import { SaveSetPayload, WorkoutSet } from '../types/api.types';
import { toApiError } from '@/shared/api/errors/to-api-error';

export async function saveSet(
  workoutId: number,
  clientId: string,
  payload: SaveSetPayload,
): Promise<WorkoutSet> {
  const response = await fetch(
    `/api/workouts/${workoutId}/sets/${encodeURIComponent(clientId)}`,
    {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(payload),
    },
  );

  if (!response.ok) {
    throw await toApiError(response);
  }

  return response.json();
}
