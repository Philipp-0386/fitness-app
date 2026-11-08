import { toApiError } from '@/shared/api/errors/to-api-error';

export async function removeExercise(workoutId: number, entryId: number): Promise<void> {
  const response = await fetch(`/api/workouts/${workoutId}/exercises/${entryId}`, {
    method: 'DELETE',
  });

  if (!response.ok) {
    throw await toApiError(response);
  }
}
