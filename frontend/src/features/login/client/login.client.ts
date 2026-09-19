import { ApiError } from '@/shared/api/errors/api-error';
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

  const body = await response.json().catch(() => null);

  if (!response.ok) {
    throw new ApiError({
      status: response.status,
      code: body?.code || 'UNKNOWN_ERROR',
      message: body?.message || 'An unknown error occurred',
      path: body?.path || null,
      fieldErrors: body?.fieldErrors || null,
      timestamp: body?.timestamp || null,
    });
  }

  return body;
}
