import 'server-only';

import backendFetch from '@/shared/api/backend';
import { toApiError } from '@/shared/api/errors/to-api-error';
import { Program, ProgramDetail } from '../types/api.types';

/**
 * Reads the programs owned by the current user.
 * @returns Returns an empty array if the user has no programs.
 */
export async function fetchUserPrograms(): Promise<Program[]> {
  const response = await backendFetch('/backend/programs');

  if (!response.ok) {
    throw await toApiError(response, 'USER_PROGRAMS_FETCH_FAILED');
  }

  return response.json();
}

/**
 * Reads a single program owned by the current user including its days.
 * @param id The id of the program to read.
 * @returns Returns an ApiError with status 404 if no program with that id is available to the user.
 */
export async function fetchUserProgramByProgramId(id: string): Promise<ProgramDetail> {
  const response = await backendFetch(`/backend/programs/${encodeURIComponent(id)}`);

  if (!response.ok) {
    throw await toApiError(response, 'USER_PROGRAM_FETCH_FAILED');
  }

  return response.json();
}

/**
 * Reads the preset programs.
 * @returns Returns an empty array if there are no preset programs.
 */
export async function fetchPresetPrograms(): Promise<Program[]> {
  const response = await backendFetch('/backend/programs/presets');

  if (!response.ok) {
    throw await toApiError(response, 'PRESET_PROGRAMS_FETCH_FAILED');
  }

  return response.json();
}

/**
 * Reads a single preset program including its days.
 * @param id The id of the preset program to read.
 * @returns Returns an ApiError with status 404 if no preset program with that id exists.
 */
export async function fetchPresetProgramByProgramId(id: string): Promise<ProgramDetail> {
  const response = await backendFetch(
    `/backend/programs/presets/${encodeURIComponent(id)}`,
  );

  if (!response.ok) {
    throw await toApiError(response, 'PRESET_PROGRAM_FETCH_FAILED');
  }

  return response.json();
}

/**
 * Reads the active program of the current user including its days.
 * @returns Returns null if the user has no active program.
 */
export async function fetchActiveProgram(): Promise<ProgramDetail | null> {
  const response = await backendFetch('/backend/programs/active');

  if (response.status === 204) {
    return null;
  }
  if (!response.ok) {
    throw await toApiError(response, 'ACTIVE_PROGRAM_FETCH_FAILED');
  }

  return response.json();
}
