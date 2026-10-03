import { cookies } from 'next/headers';
import { NextRequest, NextResponse } from 'next/server';
import {
  ACCESS_TOKEN_COOKIE,
  REFRESH_TOKEN_COOKIE,
  accessTokenCookieOptions,
  refreshTokenCookieOptions,
} from '@/shared/auth/cookies';

import { errorResponse, forwardToBackend } from '@/shared/api/forward-to-backend';

export async function POST(request: NextRequest) {
  const result = await forwardToBackend(request, '/backend/auth/login', 'POST');
  if ('error' in result) return result.error;

  const path = request.nextUrl.pathname;
  const tokens: { accessToken?: string; refreshToken?: string } | null =
    await result.response.json().catch(() => null);

  if (!tokens?.accessToken || !tokens?.refreshToken) {
    return errorResponse(
      502,
      'INVALID_BACKEND_RESPONSE',
      'Backend response missing tokens',
      path,
    );
  }

  const cookieStore = await cookies();

  cookieStore.set(ACCESS_TOKEN_COOKIE, tokens.accessToken, accessTokenCookieOptions);

  cookieStore.set(REFRESH_TOKEN_COOKIE, tokens.refreshToken, refreshTokenCookieOptions);

  return NextResponse.json({ success: true }, { status: 200 });
}
