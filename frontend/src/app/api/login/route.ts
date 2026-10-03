import { cookies } from 'next/headers';
import { NextRequest, NextResponse } from 'next/server';

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

  cookieStore.set('access_token', tokens.accessToken, {
    httpOnly: true,
    secure: process.env.NODE_ENV === 'production',
    sameSite: 'lax',
    maxAge: 60 * 15,
    path: '/',
  });

  cookieStore.set('refresh_token', tokens.refreshToken, {
    httpOnly: true,
    secure: process.env.NODE_ENV === 'production',
    sameSite: 'lax',
    maxAge: 60 * 60 * 24 * 7,
    path: '/',
  });

  return NextResponse.json({ success: true }, { status: 200 });
}
