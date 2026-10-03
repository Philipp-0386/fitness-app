import { toApiError } from '@/shared/api/errors/to-api-error';
import { LoginPayload, LoginResponse } from '../types/api.types';

/**
 * Posts the credentials to our own route handler, from the browser.
 *
 * Client side on purpose: the route handler is what sets the httpOnly token cookies, which
 * a server component cannot do.
 */
export async function login(payload: LoginPayload): Promise<LoginResponse> {
  const response = await fetch('/api/login', {
    method: 'POST',
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
