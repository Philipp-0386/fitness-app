import 'server-only';

import { NetworkError } from '@/shared/api/errors/network-error';

const BASE_URL = process.env.SPRING_API_BASE_URL;

if (!BASE_URL) {
  throw new Error('SPRING_API_BASE_URL is not defined');
}
//Server only function allowing calls to spring backend
export default async function backendFetch(
  path: string,
  init: RequestInit = {},
): Promise<Response> {
  try {
    return await fetch(`${BASE_URL}${path}`, {
      ...init,
      headers: {
        'Content-Type': 'application/json',
        ...(init.headers ?? {}),
      },
      cache: 'no-store',
    });
  } catch {
    throw new NetworkError('Backend not reachable');
  }
}
