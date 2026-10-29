import { toApiError } from '@/shared/api/errors/to-api-error';

export async function deleteProgram(id: string): Promise<void> {
  const response = await fetch(`/api/programs/${encodeURIComponent(id)}`, {
    method: 'DELETE',
  });

  if (!response.ok) {
    throw await toApiError(response);
  }
}
