import backendFetch from '@/shared/api/backend';
import { NetworkError } from '@/shared/api/errors/network-error';
import { NextRequest, NextResponse } from 'next/server';

export async function POST(request: NextRequest) {
  let requestBody: unknown;
  try {
    requestBody = await request.json();
  } catch {
    return NextResponse.json(
      {
        code: 'INVALID_JSON',
        message: 'Request body contains invalid JSON',
      },
      { status: 400 },
    );
  }

  let response: Response;
  try {
    response = await backendFetch('/backend/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(requestBody),
    });
  } catch (error) {
    if (error instanceof NetworkError) {
      return NextResponse.json(
        {
          code: 'BACKEND_UNREACHABLE',
          message: 'Spring Boot backend unreachable',
        },
        { status: 503 },
      );
    }
    return NextResponse.json(
      {
        code: 'INTERNAL_SERVER_ERROR',
        message: 'An internal server error occurred',
      },
      { status: 500 },
    );
  }

  type BackendErrorResponse = {
    status: number;
    code: string;
    message: string;
    path: string | null;
    fieldErrors: { field: string; message: string }[] | null;
  };

  if (!response.ok) {
    let error: BackendErrorResponse | null = null;
    try {
      error = await response.json();
    } catch {}

    return NextResponse.json(
      {
        code: error?.code || 'UNKNOWN_BACKEND_ERROR',
        message: error?.message || 'An unknown error occurred in the backend',
        path: error?.path || null,
        fieldErrors: error?.fieldErrors || null,
      },
      { status: response.status },
    );
  }

  let data: unknown;
  try {
    data = await response.json();
  } catch {
    data = null;
  }

  const nextRes = NextResponse.json(data, { status: response.status });
  const setCookie = response.headers.get('set-cookie');
  if (setCookie) {
    nextRes.headers.set('set-cookie', setCookie);
  }
  return nextRes;
}
