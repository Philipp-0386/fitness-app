import backendFetch from '@/shared/api/backend';
import { NetworkError } from '@/shared/api/errors/network-error';
import { cookies } from 'next/headers';
import { NextRequest, NextResponse } from 'next/server';

/**
 * Deletes the account of the current user and clears the auth cookies.
 *
 * The cookies are also cleared on 401: the token is either expired or belongs to a user
 * that no longer exists, so the browser session is unusable either way.
 */
export async function DELETE(request: NextRequest) {
  let requestBody: unknown;
  try {
    requestBody = await request.json();
  } catch {
    return NextResponse.json(
      {
        code: 'INVALID_JSON',
        message: 'Request body contains invalid JSON',
      },
      { status: 400 },
    );
  }

  let response: Response;
  try {
    response = await backendFetch('/backend/me', {
      method: 'DELETE',
      body: JSON.stringify(requestBody),
    });
  } catch (error) {
    if (error instanceof NetworkError) {
      return NextResponse.json(
        {
          code: 'BACKEND_UNREACHABLE',
          message: 'Spring Boot backend unreachable',
        },
        { status: 503 },
      );
    }
    return NextResponse.json(
      {
        code: 'INTERNAL_SERVER_ERROR',
        message: 'An internal server error occurred',
      },
      { status: 500 },
    );
  }

  if (response.ok || response.status === 401) {
    const cookieStore = await cookies();
    cookieStore.delete('access_token');
    cookieStore.delete('refresh_token');
  }

  if (!response.ok) {
    // Mirrors the backend ErrorResponse record.
    let error: {
      code: string;
      message: string;
      path: string | null;
      fieldErrors: Record<string, string> | null;
    } | null = null;
    try {
      error = await response.json();
    } catch {}

    return NextResponse.json(
      {
        code: error?.code || 'UNKNOWN_BACKEND_ERROR',
        message: error?.message || 'An unknown error occurred in the backend',
        path: error?.path || null,
        fieldErrors: error?.fieldErrors || null,
      },
      { status: response.status },
    );
  }

  return new NextResponse(null, { status: 204 });
}
