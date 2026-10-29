import { ProgramDetail, UpdateProgramPayload } from '../types/api.types';
import { toApiError } from '@/shared/api/errors/to-api-error';

export async function updateProgram(
  id: string,
  payload: UpdateProgramPayload,
): Promise<ProgramDetail> {
  const response = await fetch(`/api/programs/${encodeURIComponent(id)}`, {
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
