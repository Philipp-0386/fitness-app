import { NextRequest, NextResponse } from 'next/server';
import { cookies } from 'next/headers';

import { forwardToBackend } from '@/shared/api/forward-to-backend';
import { LOCALE_COOKIE, localeCookieOptions } from '@/shared/auth/cookies';

export async function PUT(request: NextRequest) {
  const result = await forwardToBackend(request, '/backend/me/settings', 'PUT');
  if ('error' in result) return result.error;

  const settings: { language?: string } = await result.response.json();
  (await cookies()).set(
    LOCALE_COOKIE,
    settings.language === 'DE' ? 'de' : 'en',
    localeCookieOptions,
  );

  return NextResponse.json(settings);
}
