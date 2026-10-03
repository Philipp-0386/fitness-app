import type { NextRequest } from 'next/server';
import { NextResponse } from 'next/server';

const SAFE = ['GET', 'HEAD', 'OPTIONS'];

/**
 * Proxy step: rejects state-changing requests to the route handlers that come from another origin.
 *
 * @returns a 403 response to send instead of the request, or null to let it pass
 */
export function checkOrigin(request: NextRequest): NextResponse | null {
  const isApi = request.nextUrl.pathname.startsWith('/api/');

  if (!isApi || SAFE.includes(request.method) || isSameOrigin(request)) {
    return null;
  }

  return NextResponse.json(
    {
      code: 'INVALID_ORIGIN',
      message: 'Request origin does not match the host',
      path: request.nextUrl.pathname,
    },
    { status: 403 },
  );
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
