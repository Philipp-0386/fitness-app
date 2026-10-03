import { NextRequest, NextResponse } from 'next/server';

import { forwardToBackend } from '@/shared/api/forward-to-backend';
import { clearAuthCookies } from '@/shared/auth/session';

/**
 * Deletes the account of the current user and clears the auth cookies.
 */
export async function DELETE(request: NextRequest) {
  const result = await forwardToBackend(request, '/backend/me', 'DELETE');
  if ('error' in result) return result.error;

  await clearAuthCookies();
  return new NextResponse(null, { status: 204 });
}

export async function PUT(request: NextRequest) {
  const result = await forwardToBackend(request, '/backend/me', 'PUT');
  if ('error' in result) return result.error;

  return NextResponse.json(await result.response.json());
}
