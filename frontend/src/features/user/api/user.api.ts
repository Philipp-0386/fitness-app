import 'server-only';

import backendFetch from '@/shared/api/backend';
import { toApiError } from '@/shared/api/errors/to-api-error';
import { User } from '../types/api.types';

/**
 * Reads the account of the current user.
 * Server side, because the access token lives in an httpOnly cookie that backendFetch reads.
 * @throws ApiError with status 401 if the token is missing or its user was deleted
 */
export async function fetchMe(): Promise<User> {
  const response = await backendFetch('/backend/me');

  if (!response.ok) {
    throw await toApiError(response, 'USER_FETCH_FAILED');
  }

  return response.json();
}
