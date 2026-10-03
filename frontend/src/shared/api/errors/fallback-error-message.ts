import { ApiError } from './api-error';

/**
 * Fallback, after error`s internal code could not be validated within the feature.
 */
export function fallbackErrorMessage(error: unknown): string {
  if (!(error instanceof ApiError)) {
    return 'An unexpected error occurred. Please try again later.';
  }

  switch (error.status) {
    case 400:
      return 'Please check your input.';
    case 401:
      return 'Your session has expired. Please log in again.';
    case 403:
      return 'You are not allowed to do this.';
    case 404:
      return 'This could not be found.';
    case 409:
      return 'This conflicts with existing data.';
    case 503:
      return 'The server is currently unreachable. Please try again later.';
  }

  if (error.status >= 500) {
    return 'Something went wrong on our side. Please try again later.';
  }
  return 'An unexpected error occurred. Please try again later.';
}
