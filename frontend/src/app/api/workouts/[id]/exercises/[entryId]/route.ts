import { NextRequest, NextResponse } from 'next/server';

import { forwardToBackend } from '@/shared/api/forward-to-backend';

/**
 * Swaps the exercise of an entry of a workout owned by the current user.
 */
export async function PUT(
  request: NextRequest,
  { params }: { params: Promise<{ id: string; entryId: string }> },
) {
  const { id, entryId } = await params;
  const result = await forwardToBackend(
    request,
    `/backend/workouts/${encodeURIComponent(id)}/exercises/${encodeURIComponent(entryId)}`,
    'PUT',
  );
  if ('error' in result) return result.error;

  return NextResponse.json(await result.response.json());
}

/**
 * Removes an entry from a workout owned by the current user.
 */
export async function DELETE(
  request: NextRequest,
  { params }: { params: Promise<{ id: string; entryId: string }> },
) {
  const { id, entryId } = await params;
  const result = await forwardToBackend(
    request,
    `/backend/workouts/${encodeURIComponent(id)}/exercises/${encodeURIComponent(entryId)}`,
    'DELETE',
  );
  if ('error' in result) return result.error;

  return new NextResponse(null, { status: 204 });
}
