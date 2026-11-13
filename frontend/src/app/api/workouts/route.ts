import { NextRequest, NextResponse } from 'next/server';

import { forwardToBackend } from '@/shared/api/forward-to-backend';

/**
 * Reads a page of the current user's finished workouts, used by "Load More" in the history.
 */
export async function GET(request: NextRequest) {
  const page = request.nextUrl.searchParams.get('page') ?? '0';
  const result = await forwardToBackend(
    request,
    `/backend/workouts?page=${encodeURIComponent(page)}`,
    'GET',
  );
  if ('error' in result) return result.error;

  return NextResponse.json(await result.response.json());
}

/**
 * Starts a workout from a routine or a program day of the current user.
 */
export async function POST(request: NextRequest) {
  const result = await forwardToBackend(request, '/backend/workouts', 'POST');
  if ('error' in result) return result.error;

  return NextResponse.json(await result.response.json(), {
    status: result.response.status,
  });
}
