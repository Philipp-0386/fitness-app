import { ApiError } from '@/shared/api/errors/api-error';
import { fallbackErrorMessage } from '@/shared/api/errors/fallback-error-message';

export function evaluateApiError(error: ApiError) {
  switch (error.code) {
    case 'SIGNUP_DISABLED':
      return {
        generalError: 'Sign-ups are currently closed. No account was created.',
        fieldErrors: {},
      };
    case 'USERNAME_ALREADY_TAKEN':
      return {
        generalError: 'This username is already taken. Please choose another one.',
        fieldErrors: error.fieldErrors ?? {},
      };
    case 'EMAIL_ALREADY_EXISTS':
      return {
        generalError:
          'An account with this email already exists. Please use another one.',
        fieldErrors: error.fieldErrors ?? {},
      };
    default:
      return {
        generalError: fallbackErrorMessage(error),
        fieldErrors: error.fieldErrors ?? {},
      };
  }
}
