import { ApiError } from '@/shared/api/errors/api-error';
import { SignUpPayload, SuccessfulSignUpResponse } from '../types/api.types';

/**
 * Posts the sign-up payload to our own route handler, from the browser.
 *
 * Client side, mirroring the login flow.
 */
export async function signUp(payload: SignUpPayload): Promise<SuccessfulSignUpResponse> {
  const response = await fetch('/api/signup', {
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
