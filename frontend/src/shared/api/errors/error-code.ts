/**
 * Every code an ApiError can carry.
 * Nothing compares against those, the status handles them. This is just for typesafety
 * non-string.
 */
export type ErrorCode =
  // Spring backend
  | 'INVALID_CREDENTIALS'
  | 'EMAIL_ALREADY_EXISTS'
  | 'USERNAME_ALREADY_TAKEN'
  | 'DEFAULT_ROLE_NOT_FOUND'
  | 'SIGNUP_DISABLED'
  | 'UNAUTHENTICATED'
  | 'ACCESS_DENIED'
  | 'EXERCISE_NOT_FOUND'
  | 'INVALID_PASSWORD'
  | 'INVALID_JSON'
  | 'VALIDATION_FAILED'
  | 'RESOURCE_NOT_FOUND'
  | 'INVALID_PARAMETER'
  | 'DATA_CONFLICT'
  | 'INTERNAL_ERROR'
  // Next route handlers and proxy
  | 'BACKEND_UNREACHABLE'
  | 'INTERNAL_SERVER_ERROR'
  | 'INVALID_BACKEND_RESPONSE'
  | 'INVALID_ORIGIN'
  // Fallbacks of toApiError when the body carries no code
  | 'UNKNOWN_ERROR'
  | 'USER_FETCH_FAILED'
  | 'EXERCISES_FETCH_FAILED'
  | 'EXERCISE_FETCH_FAILED';
