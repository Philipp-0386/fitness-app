import 'server-only';

import { cookies } from 'next/headers';

export type Session = {
  username: string | null;
};

/**
 * Reads the session from the httpOnly access token cookie.
 *
 * The token is NOT verified here - that happens in the Spring backend on every
 * protected request. The decoded claims are used for display purposes only.
 * See docs/security/TokenLifecycle.md for the planned lifecycle (refresh, logout).
 */
export async function getSession(): Promise<Session | null> {
  const cookieStore = await cookies();
  const accessToken = cookieStore.get('access_token')?.value;

  if (!accessToken) return null;

  return { username: readSubjectClaim(accessToken) };
}

export async function isAuthenticated(): Promise<boolean> {
  return (await getSession()) !== null;
}

function readSubjectClaim(token: string): string | null {
  const payload = token.split('.')[1];
  if (!payload) return null;

  try {
    const json = Buffer.from(payload, 'base64url').toString('utf8');
    const claims: unknown = JSON.parse(json);
    if (typeof claims === 'object' && claims !== null && 'sub' in claims) {
      const sub = (claims as { sub: unknown }).sub;
      return typeof sub === 'string' ? sub : null;
    }
    return null;
  } catch {
    return null;
  }
}
