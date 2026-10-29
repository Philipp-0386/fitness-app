import { ProgramDetail } from '../types/api.types';
import { toApiError } from '@/shared/api/errors/to-api-error';

export async function activateProgram(programId: number): Promise<ProgramDetail> {
  const response = await fetch('/api/programs/active', {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ programId }),
  });

  if (!response.ok) {
    throw await toApiError(response);
  }

  return response.json();
}
