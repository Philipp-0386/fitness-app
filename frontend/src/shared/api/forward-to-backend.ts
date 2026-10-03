import 'server-only';

import { NextRequest, NextResponse } from 'next/server';

import backendFetch from '@/shared/api/backend';
import { ErrorCode } from '@/shared/api/errors/error-code';
import { NetworkError } from '@/shared/api/errors/network-error';
import { clearAuthCookies } from '@/shared/auth/session';

type ForwardResult = { response: Response } | { error: NextResponse };

/**
 * Forwards a route handler's JSON body to the backend.
 *
 * Returns the backend response if it is ok, otherwise an error response,
 * ready to be returned by the route handler. 401 clears the auth cookies, because the session
 * is unusable.
 */
export async function forwardToBackend(
  request: NextRequest,
  backendPath: string,
  method: string,
): Promise<ForwardResult> {
  const path = request.nextUrl.pathname;

  let body: unknown;
  try {
    body = await request.json();
  } catch {
    return {
      error: errorResponse(
        400,
        'INVALID_JSON',
        'Request body contains invalid JSON',
        path,
      ),
    };
  }

  let response: Response;
  try {
    response = await backendFetch(backendPath, { method, body: JSON.stringify(body) });
  } catch (error) {
    if (error instanceof NetworkError) {
      return {
        error: errorResponse(503, 'BACKEND_UNREACHABLE', 'Backend unreachable', path),
      };
    }
    return {
      error: errorResponse(500, 'INTERNAL_ERROR', 'An unexpected error occurred', path),
    };
  }

  if (response.ok) {
    return { response };
  }

  if (response.status === 401) {
    await clearAuthCookies();
  }

  const errorBody = await response.json().catch(() => null);
  if (!errorBody?.code) {
    return {
      error: errorResponse(
        response.status,
        'UNKNOWN_ERROR',
        'An unknown error occurred',
        path,
      ),
    };
  }
  return { error: NextResponse.json(errorBody, { status: response.status }) };
}

/** An error response in the same shape the backend's ErrorResponse has. */
export function errorResponse(
  status: number,
  code: ErrorCode,
  message: string,
  path: string,
): NextResponse {
  return NextResponse.json(
    { code, message, path, fieldErrors: null, timestamp: new Date().toISOString() },
    { status },
  );
}
