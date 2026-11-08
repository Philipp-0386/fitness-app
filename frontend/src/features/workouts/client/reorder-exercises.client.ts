import { toApiError } from '@/shared/api/errors/to-api-error';

export async function reorderExercises(
  workoutId: number,
  entryIds: number[],
): Promise<void> {
  const response = await fetch(`/api/workouts/${workoutId}/exercises/order`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ entryIds }),
  });

  if (!response.ok) {
    throw await toApiError(response);
  }
}
