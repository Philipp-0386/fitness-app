import { cookies } from 'next/headers';
import { NextRequest, NextResponse } from 'next/server';

import { errorResponse } from '@/shared/api/forward-to-backend';
import { LOCALE_COOKIE, localeCookieOptions } from '@/shared/auth/cookies';

/**
 * Stores the picked language in the locale cookie. Doenst need session
 */
export async function POST(request: NextRequest) {
  const body: { locale?: unknown } | null = await request.json().catch(() => null);
  const locale = body?.locale;

  if (locale !== 'en' && locale !== 'de') {
    return errorResponse(
      400,
      'VALIDATION_FAILED',
      'Locale must be en or de',
      request.nextUrl.pathname,
    );
  }

  (await cookies()).set(LOCALE_COOKIE, locale, localeCookieOptions);

  return new NextResponse(null, { status: 204 });
}