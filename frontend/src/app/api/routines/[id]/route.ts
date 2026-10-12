import { NextRequest, NextResponse } from 'next/server';

import { forwardToBackend } from '@/shared/api/forward-to-backend';

/**
 * Replaces name, description and exercises of a routine owned by the current user.
 */
export async function PUT(
  request: NextRequest,
  { params }: { params: Promise<{ id: string }> },
) {
  const { id } = await params;
  const result = await forwardToBackend(
    request,
    `/backend/routines/${encodeURIComponent(id)}`,
    'PUT',
  );
  if ('error' in result) return result.error;

  return NextResponse.json(await result.response.json());
}

/**
 * Deletes a routine owned by the current user.
 */
export async function DELETE(
  request: NextRequest,
  { params }: { params: Promise<{ id: string }> },
) {
  const { id } = await params;
  const result = await forwardToBackend(
    request,
    `/backend/routines/${encodeURIComponent(id)}`,
    'DELETE',
  );
  if ('error' in result) return result.error;

  return new NextResponse(null, { status: 204 });
}
