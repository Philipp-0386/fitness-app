import { NextRequest, NextResponse } from 'next/server';

import { forwardToBackend } from '@/shared/api/forward-to-backend';

/**
 * Finishes a running workout owned by the current user.
 */
export async function POST(
  request: NextRequest,
  { params }: { params: Promise<{ id: string }> },
) {
  const { id } = await params;
  const result = await forwardToBackend(
    request,
    `/backend/workouts/${encodeURIComponent(id)}/finish`,
    'POST',
  );
  if ('error' in result) return result.error;

  return new NextResponse(null, { status: 204 });
}
