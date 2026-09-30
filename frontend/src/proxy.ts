import type { NextRequest } from 'next/server';
import { NextResponse } from 'next/server';

import {
  ACCESS_TOKEN_COOKIE,
  REFRESH_TOKEN_COOKIE,
  accessTokenCookieOptions,
} from '@/shared/auth/cookies';
import { refreshAccessToken } from '@/shared/auth/refresh';
import { isExpiringSoon } from '@/shared/auth/session';

const SAFE = ['GET', 'HEAD', 'OPTIONS'];

export async function proxy(request: NextRequest) {
  const isApi = request.nextUrl.pathname.startsWith('/api/');

  if (isApi && !SAFE.includes(request.method) && !isSameOrigin(request)) {
    return NextResponse.json(
      {
        code: 'INVALID_ORIGIN',
        message: 'Request origin does not match the host',
        path: request.nextUrl.pathname,
      },
      { status: 403 },
    );
  }

  return refreshSession(request);
}

/**
 * The new token is written to the forwarded request as well, so the code rendering this very
 * request already sees it.
 */
async function refreshSession(request: NextRequest): Promise<NextResponse> {
  const accessToken = request.cookies.get(ACCESS_TOKEN_COOKIE)?.value;
  const refreshToken = request.cookies.get(REFRESH_TOKEN_COOKIE)?.value;

  if (!refreshToken) {
    return NextResponse.next();
  }
  if (accessToken && !isExpiringSoon(accessToken)) {
    return NextResponse.next();
  }

  const result = await refreshAccessToken(refreshToken);

  switch (result.status) {
    case 'ok': {
      request.cookies.set(ACCESS_TOKEN_COOKIE, result.accessToken);
      const response = NextResponse.next({ request: { headers: request.headers } });
      response.cookies.set(
        ACCESS_TOKEN_COOKIE,
        result.accessToken,
        accessTokenCookieOptions,
      );
      return response;
    }
    case 'invalid': {
      request.cookies.delete(ACCESS_TOKEN_COOKIE);
      request.cookies.delete(REFRESH_TOKEN_COOKIE);
      const response = NextResponse.next({ request: { headers: request.headers } });
      response.cookies.delete(ACCESS_TOKEN_COOKIE);
      response.cookies.delete(REFRESH_TOKEN_COOKIE);
      return response;
    }
    case 'unavailable':
      return NextResponse.next();
  }
}

function isSameOrigin(request: NextRequest): boolean {
  const origin = request.headers.get('origin');
  const host = request.headers.get('host');
  if (!origin || !host) {
    return false;
  }

  try {
    return new URL(origin).host === host;
  } catch {
    return false;
  }
}

export const config = {
  matcher: '/((?!_next/static|_next/image|favicon.ico).*)',
};
