import { UpdatePasswordPayload } from '../types/api.types';
import { toApiError } from '@/shared/api/errors/to-api-error';

export async function updatePassword(payload: UpdatePasswordPayload): Promise<void> {
  const response = await fetch('/api/me/password', {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  });

  if (!response.ok) {
    throw await toApiError(response);
  }
}
