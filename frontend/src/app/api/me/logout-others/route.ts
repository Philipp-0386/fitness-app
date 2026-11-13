import { cookies } from 'next/headers';
import { NextRequest, NextResponse } from 'next/server';

import { errorResponse, forwardToBackend } from '@/shared/api/forward-to-backend';
import {
  ACCESS_TOKEN_COOKIE,
  REFRESH_TOKEN_COOKIE,
  accessTokenCookieOptions,
  refreshTokenCookieOptions,
} from '@/shared/auth/cookies';

/**
 * Ends every session in the backend. The response carries a token pair of a new
 * session, stored here so this browser stays logged in.
 */
export async function POST(request: NextRequest) {
  const result = await forwardToBackend(request, '/backend/me/logout-others', 'POST');
  if ('error' in result) return result.error;

  const tokens: { accessToken?: string; refreshToken?: string } | null =
    await result.response.json().catch(() => null);

  if (!tokens?.accessToken || !tokens?.refreshToken) {
    return errorResponse(
      502,
      'INVALID_BACKEND_RESPONSE',
      'Backend response missing tokens',
      request.nextUrl.pathname,
    );
  }

  const cookieStore = await cookies();
  cookieStore.set(ACCESS_TOKEN_COOKIE, tokens.accessToken, accessTokenCookieOptions);
  cookieStore.set(REFRESH_TOKEN_COOKIE, tokens.refreshToken, refreshTokenCookieOptions);

  return new NextResponse(null, { status: 204 });
}
