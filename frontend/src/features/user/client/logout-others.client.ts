import { toApiError } from '@/shared/api/errors/to-api-error';

export async function logoutOthers(): Promise<void> {
  const response = await fetch('/api/me/logout-others', { method: 'POST' });

  if (!response.ok) {
    throw await toApiError(response);
  }
}
