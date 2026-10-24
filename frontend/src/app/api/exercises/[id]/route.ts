import { NextRequest, NextResponse } from 'next/server';

import { forwardToBackend } from '@/shared/api/forward-to-backend';

/**
 * Replaces all fields of an exercise owned by the current user.
 */
export async function PUT(
  request: NextRequest,
  { params }: { params: Promise<{ id: string }> },
) {
  const { id } = await params;
  const result = await forwardToBackend(
    request,
    `/backend/exercises/${encodeURIComponent(id)}`,
    'PUT',
  );
  if ('error' in result) return result.error;

  return NextResponse.json(await result.response.json());
}

/**
 * Deletes an exercise owned by the current user.
 */
export async function DELETE(
  request: NextRequest,
  { params }: { params: Promise<{ id: string }> },
) {
  const { id } = await params;
  const result = await forwardToBackend(
    request,
    `/backend/exercises/${encodeURIComponent(id)}`,
    'DELETE',
  );
  if ('error' in result) return result.error;

  return new NextResponse(null, { status: 204 });
}
