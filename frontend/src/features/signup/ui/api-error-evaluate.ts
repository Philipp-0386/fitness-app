import { ApiError } from '@/shared/api/errors/api-error';

/**
 * Maps a sign-up error to the message key of its general error, null for the generic fallback.
 */
export function evaluateApiError(error: ApiError) {
  switch (error.code) {
    case 'SIGNUP_DISABLED':
      return { generalError: 'signupDisabled', fieldErrors: {} } as const;
    case 'USERNAME_ALREADY_TAKEN':
      return {
        generalError: 'usernameTaken',
        fieldErrors: error.fieldErrors ?? {},
      } as const;
    case 'EMAIL_ALREADY_EXISTS':
      return {
        generalError: 'emailTaken',
        fieldErrors: error.fieldErrors ?? {},
      } as const;
    default:
      return { generalError: null, fieldErrors: error.fieldErrors ?? {} } as const;
  }
}
