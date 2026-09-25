import { ApiError } from '@/shared/api/errors/api-error';
import { DeleteUserPayload } from '../types/api.types';

/**
 * Asks our own route handler to delete the account, from the browser.
 *
 * Client side like login: the route handler also clears the httpOnly token cookies.
 */
export async function deleteMe(payload: DeleteUserPayload): Promise<void> {
  const response = await fetch('/api/me', {
    method: 'DELETE',
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
