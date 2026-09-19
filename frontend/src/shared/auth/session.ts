import 'server-only';

import { cookies } from 'next/headers';
import { redirect } from 'next/navigation';

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

/**
 * The gate for a protected page: returns the session, or redirects to the login page.
 *
 * Server side only, and it never returns null - so a page can use the session right away
 * instead of narrowing it first.
 */
export async function requireSession(): Promise<Session> {
  const session = await getSession();

  if (!session) {
    redirect('/login');
  }

  return session;
}

/**
 * The gate for a page that only makes sense while logged out, such as login and sign-up.
 *
 * Redirects to the dashboard when a session exists, and does nothing otherwise.
 */
export async function redirectIfAuthenticated(): Promise<void> {
  if (await isAuthenticated()) {
    redirect('/dashboard');
  }
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
