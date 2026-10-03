import 'server-only';

import backendFetch from '@/shared/api/backend';
import { ErrorCode } from '@/shared/api/errors/error-code';
import { toApiError } from '@/shared/api/errors/to-api-error';
import { Exercise } from '../types/api.types';

/**
 * Reads the exercises available to the current user.
 *
 * Server side, because the access token lives in an httpOnly cookie that backendFetch reads.
 */
export async function fetchExercises(): Promise<Exercise[]> {
  return request('/backend/exercises', 'EXERCISES_FETCH_FAILED');
}

/**
 * Reads a single exercise.
 *
 * The id is passed on as given, so a malformed one reaches the backend and surfaces its response
 * rather than being rejected here.
 *
 * @throws ApiError with status 404 if no exercise with that id is available to the user, whether
 *         because it does not exist, is deleted or belongs to someone else
 */
export async function fetchExerciseById(id: string): Promise<Exercise> {
  return request(`/backend/exercises/${encodeURIComponent(id)}`, 'EXERCISE_FETCH_FAILED');
}

async function request<T>(path: string, fallbackCode: ErrorCode): Promise<T> {
  const response = await backendFetch(path);

  if (!response.ok) {
    throw await toApiError(response, fallbackCode);
  }

  return response.json();
}
