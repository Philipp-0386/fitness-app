import 'server-only';

import backendFetch from '@/shared/api/backend';
import { ApiError } from '@/shared/api/errors/api-error';
import { User } from '../types/api.types';

/**
 * Reads the account of the current user.
 * Server side, because the access token lives in an httpOnly cookie that backendFetch reads.
 * @throws ApiError with status 401 if the token is missing or its user was deleted
 */
export async function fetchMe(): Promise<User> {
  const response = await backendFetch('/backend/me');
  const body = await response.json().catch(() => null);

  if (!response.ok) {
    throw new ApiError({
      status: response.status,
      code: body?.code || 'USER_FETCH_FAILED',
      message: body?.message || 'An unknown error occurred',
      path: body?.path || null,
      timestamp: body?.timestamp || null,
    });
  }

  return body;
}
