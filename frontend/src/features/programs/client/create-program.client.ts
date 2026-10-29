import { CreateProgramPayload, ProgramDetail } from '../types/api.types';
import { toApiError } from '@/shared/api/errors/to-api-error';

export async function createProgram(
  payload: CreateProgramPayload,
): Promise<ProgramDetail> {
  const response = await fetch('/api/programs', {
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
