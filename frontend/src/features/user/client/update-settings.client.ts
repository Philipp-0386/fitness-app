import { UpdateUserSettingsPayload, UserSettings } from '../types/api.types';
import { toApiError } from '@/shared/api/errors/to-api-error';

export async function updateSettings(
  payload: UpdateUserSettingsPayload,
): Promise<UserSettings> {
  const response = await fetch('/api/me/settings', {
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
