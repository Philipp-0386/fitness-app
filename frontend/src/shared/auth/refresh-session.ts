import type { NextRequest } from 'next/server';
import { NextResponse } from 'next/server';

import {
  ACCESS_TOKEN_COOKIE,
  REFRESH_TOKEN_COOKIE,
  accessTokenCookieOptions,
} from './cookies';
import { refreshAccessToken } from './refresh';
import { isExpiringSoon } from './token';

/**
 * Proxy step: renews the access token when it is missing or about to expire.
 * Token is written to the forwarded request as well, so the code rendering this
 * request already sees it.
 */
export async function refreshSession(request: NextRequest): Promise<NextResponse> {
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
