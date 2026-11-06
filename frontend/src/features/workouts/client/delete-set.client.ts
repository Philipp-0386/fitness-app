import { toApiError } from '@/shared/api/errors/to-api-error';

export async function deleteSet(workoutId: number, clientId: string): Promise<void> {
  const response = await fetch(
    `/api/workouts/${workoutId}/sets/${encodeURIComponent(clientId)}`,
    { method: 'DELETE' },
  );

  if (!response.ok) {
    throw await toApiError(response);
  }
}
