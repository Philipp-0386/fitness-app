import { NextRequest, NextResponse } from 'next/server';

import { forwardToBackend } from '@/shared/api/forward-to-backend';

/**
 * Deletes a program owned by the current user.
 */
export async function DELETE(
  request: NextRequest,
  { params }: { params: Promise<{ id: string }> },
) {
  const { id } = await params;
  const result = await forwardToBackend(
    request,
    `/backend/programs/${encodeURIComponent(id)}`,
    'DELETE',
  );
  if ('error' in result) return result.error;

  return new NextResponse(null, { status: 204 });
}
