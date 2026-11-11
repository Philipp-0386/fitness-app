import { NextRequest, NextResponse } from 'next/server';
import { forwardToBackend } from '@/shared/api/forward-to-backend';

export async function PUT(request: NextRequest) {
  const result = await forwardToBackend(request, '/backend/me/settings', 'PUT');
  if ('error' in result) return result.error;

  return NextResponse.json(await result.response.json());
}
