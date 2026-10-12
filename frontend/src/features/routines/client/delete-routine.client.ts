import { toApiError } from '@/shared/api/errors/to-api-error';

export async function deleteRoutine(id: string): Promise<void> {
  const response = await fetch(`/api/routines/${encodeURIComponent(id)}`, {
    method: 'DELETE',
  });

  if (!response.ok) {
    throw await toApiError(response);
  }
}
