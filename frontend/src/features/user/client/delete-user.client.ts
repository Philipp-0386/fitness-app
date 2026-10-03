import { toApiError } from '@/shared/api/errors/to-api-error';
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
    throw await toApiError(response);
  }
}
