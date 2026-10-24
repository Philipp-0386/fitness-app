import { NextRequest, NextResponse } from 'next/server';

import { forwardToBackend } from '@/shared/api/forward-to-backend';

/**
 * Creates an exercise owned by the current user.
 */
export async function POST(request: NextRequest) {
  const result = await forwardToBackend(request, '/backend/exercises', 'POST');
  if ('error' in result) return result.error;

  return NextResponse.json(await result.response.json(), {
    status: result.response.status,
  });
}
