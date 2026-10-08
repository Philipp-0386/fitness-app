import 'server-only';

import backendFetch from '@/shared/api/backend';
import { Routine, RoutineDetail } from '../types/api.types';
import { ErrorCode } from '@/shared/api/errors/error-code';
import { toApiError } from '@/shared/api/errors/to-api-error';

async function request<T>(path: string, fallbackCode: ErrorCode): Promise<T> {
  const response = await backendFetch(path);

  if (!response.ok) {
    throw await toApiError(response, fallbackCode);
  }

  return response.json();
}

/**
 * Reads the routines available and owned by the current user.
 * @returns Returns an empty array if the user has no routines.
 */
export async function fetchUserRoutines(): Promise<Routine[]> {
  return request('/backend/routines', 'USER_ROUTINES_FETCH_FAILED');
}

/**
 * Reads a single routine owned by the current user.
 * @param id The id of the routine to read.
 * @returns Returns an ApiError with status 404 if no routine with that id is available to the user.
 */
export async function fetchUserRoutineByRoutineId(id: string): Promise<RoutineDetail> {
  return request(
    `/backend/routines/${encodeURIComponent(id)}`,
    'USER_ROUTINE_FETCH_FAILED',
  );
}

/**
 * Reads the preset routines available to the current user.
 * @returns Returns an empty array if the user has no preset routines.
 */
export async function fetchPresetRoutines(): Promise<Routine[]> {
  return request('/backend/routines/presets', 'PRESET_ROUTINES_FETCH_FAILED');
}

/**
 * Reads a single preset routine available to the current user.
 * @param id The id of the preset routine to read.
 * @returns Returns an ApiError with status 404 if no preset routine with that id is available to the user.
 */
export async function fetchPresetRoutineByRoutineId(id: string): Promise<RoutineDetail> {
  return request(
    `/backend/routines/presets/${encodeURIComponent(id)}`,
    'PRESET_ROUTINE_FETCH_FAILED',
  );
}
