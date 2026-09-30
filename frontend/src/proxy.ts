import type { NextRequest } from 'next/server';

import { refreshSession } from '@/shared/auth/refresh-session';
import { checkOrigin } from '@/shared/security/origin-check';

export async function proxy(request: NextRequest) {
  const rejection = checkOrigin(request);
  if (rejection) return rejection;

  return refreshSession(request);
}

export const config = {
  matcher: '/((?!_next/static|_next/image|favicon.ico).*)',
};
