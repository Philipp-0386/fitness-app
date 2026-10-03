import { NextResponse } from 'next/server';
import { ACCESS_TOKEN_COOKIE, REFRESH_TOKEN_COOKIE } from '@/shared/auth/cookies';

import { clearAuthCookies } from '@/shared/auth/session';

/**
 * Clears the auth cookies of the current browser session.
 *
 * The backend has no /backend/auth/logout endpoint yet, so the refresh token
 * stays valid until it expires (see docs/security/TokenLifecycle.md, gap #6).
 * Once the endpoint exists, it has to be called from here as well.
 */
export async function POST() {
  await clearAuthCookies();

  return NextResponse.json({ success: true }, { status: 200 });
}
