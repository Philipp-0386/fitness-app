//place to define uniform cookie structure and naming

export const ACCESS_TOKEN_COOKIE = 'access_token';

export const REFRESH_TOKEN_COOKIE = 'refresh_token';

export const accessTokenCookieOptions = {
  httpOnly: true,
  secure: process.env.NODE_ENV === 'production',
  sameSite: 'lax',
  maxAge: 60 * 15,
  path: '/',
} as const;

export const refreshTokenCookieOptions = {
  httpOnly: true,
  secure: process.env.NODE_ENV === 'production',
  sameSite: 'lax',
  maxAge: 60 * 60 * 24 * 7,
  path: '/',
} as const;
