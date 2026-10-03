const BASE_URL = process.env.SPRING_API_BASE_URL;

if (!BASE_URL) {
  throw new Error('SPRING_API_BASE_URL is not defined');
}

export type RefreshResult =
  | { status: 'ok'; accessToken: string }
  | { status: 'invalid' }
  | { status: 'unavailable' };

export async function refreshAccessToken(refreshToken: string): Promise<RefreshResult> {
  let res: Response;
  try {
    res = await fetch(`${BASE_URL}/backend/auth/refresh`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ refreshToken }),
      cache: 'no-store',
    });
  } catch {
    return { status: 'unavailable' };
  }

  if (res.status >= 500) {
    return { status: 'unavailable' };
  }
  if (!res.ok) {
    return { status: 'invalid' };
  }

  const body = await res.json().catch(() => null);
  if (typeof body?.accessToken !== 'string') {
    return { status: 'unavailable' };
  }

  return { status: 'ok', accessToken: body.accessToken };
}
