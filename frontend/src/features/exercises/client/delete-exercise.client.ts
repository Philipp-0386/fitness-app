import { toApiError } from '@/shared/api/errors/to-api-error';

export async function deleteExercise(id: string): Promise<void> {
  const response = await fetch(`/api/exercises/${encodeURIComponent(id)}`, {
    method: 'DELETE',
  });

  if (!response.ok) {
    throw await toApiError(response);
  }
}
