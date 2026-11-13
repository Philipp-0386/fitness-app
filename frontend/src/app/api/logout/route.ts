import { cookies } from 'next/headers';
import { NextResponse } from 'next/server';

import backendFetch from '@/shared/api/backend';
import { REFRESH_TOKEN_COOKIE } from '@/shared/auth/cookies';
import { clearAuthCookies } from '@/shared/auth/session';

/**
 * Ends the session in the backend, then clears the auth cookies of this browser.
 *
 * The cookies are cleared even if the backend call fails, the user is logged out
 * locally either way. Only the server side revocation is lost in that case.
 */
export async function POST() {
  const refreshToken = (await cookies()).get(REFRESH_TOKEN_COOKIE)?.value;

  if (refreshToken) {
    try {
      await backendFetch('/backend/auth/logout', {
        method: 'POST',
        body: JSON.stringify({ refreshToken }),
      });
    } catch {
      // backend unreachable, still log out locally
    }
  }

  await clearAuthCookies();

  return NextResponse.json({ success: true }, { status: 200 });
}
