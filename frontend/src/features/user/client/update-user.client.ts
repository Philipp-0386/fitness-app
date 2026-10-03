import { UpdateUserPayload } from '../types/api.types';
import { User } from '../types/api.types';
import { toApiError } from '@/shared/api/errors/to-api-error';

export async function updateMe(payload: UpdateUserPayload): Promise<User> {
  const response = await fetch('/api/me', {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  });

  if (!response.ok) {
    throw await toApiError(response);
  }

  return response.json();
}
