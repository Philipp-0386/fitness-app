/**
 * Every code an ApiError can carry, so a comparison against a misspelled code fails to compile.
 * Codes missing here (e.g. METHOD_NOT_ALLOWED from Spring's own exceptions) can still arrive at
 * runtime. Nothing compares against those, the status handles them.
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
  | 'PRESET_ROUTINE_NOT_FOUND'
  | 'USER_ROUTINE_NOT_FOUND'
  | 'INVALID_PASSWORD'
  | 'INVALID_JSON'
  | 'VALIDATION_FAILED'
  | 'RESOURCE_NOT_FOUND'
  | 'INVALID_PARAMETER'
  | 'DATA_CONFLICT'
  | 'INTERNAL_ERROR'
  // Next route handlers and proxy
  | 'BACKEND_UNREACHABLE'
  | 'INVALID_BACKEND_RESPONSE'
  | 'INVALID_ORIGIN'
  // Fallbacks when a body carries no code
  | 'UNKNOWN_ERROR'
  | 'USER_FETCH_FAILED'
  | 'EXERCISES_FETCH_FAILED'
  | 'EXERCISE_FETCH_FAILED'
  | 'USER_ROUTINES_FETCH_FAILED'
  | 'USER_ROUTINE_FETCH_FAILED'
  | 'PRESET_ROUTINES_FETCH_FAILED'
  | 'PRESET_ROUTINE_FETCH_FAILED';
