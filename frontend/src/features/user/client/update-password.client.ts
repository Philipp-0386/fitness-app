import { UpdatePasswordPayload } from '../types/api.types';
import { ApiError } from '@/shared/api/errors/api-error';

export async function updatePassword(payload: UpdatePasswordPayload): Promise<void> {
  const response = await fetch('/api/me/password', {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  });

  if (!response.ok) {
    const body = await response.json().catch(() => null);
    throw new ApiError({
      status: response.status,
      code: body?.code || 'UNKNOWN_ERROR',
      message: body?.message || 'An unknown error occurred',
      path: body?.path || null,
      fieldErrors: body?.fieldErrors || null,
      timestamp: body?.timestamp || null,
    });
  }
}
