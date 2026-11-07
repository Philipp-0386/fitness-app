import { toApiError } from '@/shared/api/errors/to-api-error';

/** Finishes the workout. An already finished one counts as success, e.g. after a retried request. */
export async function finishWorkout(workoutId: number): Promise<void> {
  const response = await fetch(`/api/workouts/${workoutId}/finish`, { method: 'POST' });

  if (!response.ok) {
    const error = await toApiError(response);
    if (error.code === 'WORKOUT_NOT_IN_PROGRESS') return;
    throw error;
  }
}
