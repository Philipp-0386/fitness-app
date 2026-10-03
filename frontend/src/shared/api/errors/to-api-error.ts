import { ApiError } from './api-error';
import { ErrorCode } from './error-code';

/**
 * Turns a failed response into an ApiError
 */
export async function toApiError(
  response: Response,
  fallbackCode: ErrorCode = 'UNKNOWN_ERROR',
): Promise<ApiError> {
  const body = await response.json().catch(() => null);

  return new ApiError({
    status: response.status,
    code: body?.code || fallbackCode,
    message: body?.message || 'An unknown error occurred',
    path: body?.path || null,
    fieldErrors: body?.fieldErrors || null,
    timestamp: body?.timestamp || null,
  });
}
