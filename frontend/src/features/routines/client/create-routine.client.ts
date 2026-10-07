import { CreateRoutinePayload, RoutineDetail } from '../types/api.types';
import { toApiError } from '@/shared/api/errors/to-api-error';

export async function createRoutine(
  payload: CreateRoutinePayload,
): Promise<RoutineDetail> {
  const response = await fetch('/api/routines', {
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
