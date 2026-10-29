import { NextRequest, NextResponse } from 'next/server';

import { forwardToBackend } from '@/shared/api/forward-to-backend';

/**
 * Makes a program owned by the current user the active one.
 */
export async function PUT(request: NextRequest) {
  const result = await forwardToBackend(request, '/backend/programs/active', 'PUT');
  if ('error' in result) return result.error;

  return NextResponse.json(await result.response.json());
}
