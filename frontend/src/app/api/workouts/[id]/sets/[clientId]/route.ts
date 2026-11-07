import { NextRequest, NextResponse } from 'next/server';

import { forwardToBackend } from '@/shared/api/forward-to-backend';

/**
 * Logs or replaces a set of a workout owned by the current user.
 */
export async function PUT(
  request: NextRequest,
  { params }: { params: Promise<{ id: string; clientId: string }> },
) {
  const { id, clientId } = await params;
  const result = await forwardToBackend(
    request,
    `/backend/workouts/${encodeURIComponent(id)}/sets/${encodeURIComponent(clientId)}`,
    'PUT',
  );
  if ('error' in result) return result.error;

  return NextResponse.json(await result.response.json());
}

/**
 * Deletes a set of a workout owned by the current user.
 */
export async function DELETE(
  request: NextRequest,
  { params }: { params: Promise<{ id: string; clientId: string }> },
) {
  const { id, clientId } = await params;
  const result = await forwardToBackend(
    request,
    `/backend/workouts/${encodeURIComponent(id)}/sets/${encodeURIComponent(clientId)}`,
    'DELETE',
  );
  if ('error' in result) return result.error;

  return new NextResponse(null, { status: 204 });
}
