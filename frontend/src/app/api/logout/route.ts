import { cookies } from 'next/headers';
import { NextResponse } from 'next/server';

/**
 * Clears the auth cookies of the current browser session.
 *
 * The backend has no /backend/auth/logout endpoint yet, so the refresh token
 * stays valid until it expires (see docs/security/TokenLifecycle.md, gap #6).
 * Once the endpoint exists, it has to be called from here as well.
 */
export async function POST() {
  const cookieStore = await cookies();

  cookieStore.delete('access_token');
  cookieStore.delete('refresh_token');

  return NextResponse.json({ success: true }, { status: 200 });
}
