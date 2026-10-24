/**
 * Every code an ApiError can carry, so a comparison against a misspelled code fails to compile.
 * Codes missing here (e.g. METHOD_NOT_ALLOWED from Spring's own exceptions) can still arrive at
 * runtime. Nothing compares against those, the status handles them.
 */
export type ErrorCode =
  // Spring backend (GlobalExceptionHandler, any endpoint)
  | 'DATA_CONFLICT'
  | 'INTERNAL_ERROR'
  | 'INVALID_JSON'
  | 'INVALID_PARAMETER'
  | 'RESOURCE_NOT_FOUND'
  | 'VALIDATION_FAILED'
  // Spring backend (auth)
  | 'ACCESS_DENIED'
  | 'INVALID_CREDENTIALS'
  | 'UNAUTHENTICATED'
  // Spring backend (signup)
  | 'SIGNUP_DISABLED'
  // Spring backend (user)
  | 'DEFAULT_ROLE_NOT_FOUND'
  | 'EMAIL_ALREADY_EXISTS'
  | 'INVALID_PASSWORD'
  | 'USERNAME_ALREADY_TAKEN'
  // Spring backend (exercise)
  | 'EXERCISE_NOT_FOUND'
  | 'GLOBAL_EXERCISE_NOT_EDITABLE'
  | 'TRACKING_TYPE_LOCKED'
  // Spring backend (routine)
  | 'PRESET_ROUTINE_NOT_FOUND'
  | 'USER_ROUTINE_NOT_FOUND'
  // Next route handlers and proxy
  | 'BACKEND_UNREACHABLE'
  | 'INVALID_BACKEND_RESPONSE'
  | 'INVALID_ORIGIN'
  // Fallbacks when a body carries no code
  | 'UNKNOWN_ERROR'
  | 'USER_FETCH_FAILED'
  | 'EXERCISE_FETCH_FAILED'
  | 'EXERCISES_FETCH_FAILED'
  | 'MUSCLE_GROUPS_FETCH_FAILED'
  | 'PRESET_ROUTINE_FETCH_FAILED'
  | 'PRESET_ROUTINES_FETCH_FAILED'
  | 'USER_ROUTINE_FETCH_FAILED'
  | 'USER_ROUTINES_FETCH_FAILED';
