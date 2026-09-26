import { ApiError } from '@/shared/api/errors/api-error';

export function evaluateApiError(error: ApiError) {
  switch (error.code) {
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
    case 'DEFAULT_ROLE_NOT_FOUND':
      return {
        generalError:
          'Internal error: Default user role not found. Please contact support.',
        fieldErrors: {},
      };
    case 'BACKEND_UNREACHABLE':
      return {
        generalError:
          'The server currently seems to be unreachable. Please try again later.',
        fieldErrors: {},
      };
    default:
      return {
        generalError: error.message || 'An unknown error occurred.',
        fieldErrors: error.fieldErrors ?? {},
      };
  }
}
