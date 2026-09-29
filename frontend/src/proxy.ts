import type { NextRequest } from 'next/server';
import { NextResponse } from 'next/server';

const SAFE = ['GET', 'HEAD', 'OPTIONS'];

export function proxy(request: NextRequest) {
  if (SAFE.includes(request.method)) {
    return;
  }

  if (!isSameOrigin(request)) {
    return NextResponse.json(
      {
        code: 'INVALID_ORIGIN',
        message: 'Request origin does not match the host',
        path: request.nextUrl.pathname,
      },
      { status: 403 },
    );
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
  matcher: '/api/:path*',
};
