import { WorkoutExercise } from '../types/api.types';
import { toApiError } from '@/shared/api/errors/to-api-error';

export async function addExercise(
  workoutId: number,
  exerciseId: number,
): Promise<WorkoutExercise> {
  const response = await fetch(`/api/workouts/${workoutId}/exercises`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ exerciseId }),
  });

  if (!response.ok) {
    throw await toApiError(response);
  }

  return response.json();
}
