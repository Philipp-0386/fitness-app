import { RoutineDetail, UpdateRoutinePayload } from '../types/api.types';
import { toApiError } from '@/shared/api/errors/to-api-error';

export async function updateRoutine(
  id: string,
  payload: UpdateRoutinePayload,
): Promise<RoutineDetail> {
  const response = await fetch(`/api/routines/${encodeURIComponent(id)}`, {
    method: 'PUT',
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
