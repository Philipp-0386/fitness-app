import { NextRequest, NextResponse } from 'next/server';

import { forwardToBackend } from '@/shared/api/forward-to-backend';

/**
 * Sets the complete new order of the entries of a workout owned by the current user.
 */
export async function PUT(
  request: NextRequest,
  { params }: { params: Promise<{ id: string }> },
) {
  const { id } = await params;
  const result = await forwardToBackend(
    request,
    `/backend/workouts/${encodeURIComponent(id)}/exercises/order`,
    'PUT',
  );
  if ('error' in result) return result.error;

  return new NextResponse(null, { status: 204 });
}
