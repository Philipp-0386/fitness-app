import { toApiError } from '@/shared/api/errors/to-api-error';

export async function discardWorkout(workoutId: number): Promise<void> {
  const response = await fetch(`/api/workouts/${workoutId}`, { method: 'DELETE' });

  if (!response.ok) {
    throw await toApiError(response);
  }
}
