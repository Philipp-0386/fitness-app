import { toApiError } from '@/shared/api/errors/to-api-error';
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

  if (!response.ok) {
    throw await toApiError(response);
  }

  return response.json();
}
