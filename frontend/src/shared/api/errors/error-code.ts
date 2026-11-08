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
  | 'TOO_MANY_LOGIN_ATTEMPTS'
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
  // Spring backend (program)
  | 'PRESET_PROGRAM_NOT_FOUND'
  | 'PROGRAM_DAY_NOT_FOUND'
  | 'PROGRAM_LIMIT_REACHED'
  | 'USER_PROGRAM_NOT_FOUND'
  // Spring backend (workout)
  | 'EXERCISE_ALREADY_IN_WORKOUT'
  | 'REST_DAY_NOT_STARTABLE'
  | 'SET_NUMBER_TAKEN'
  | 'WORKOUT_ALREADY_ACTIVE'
  | 'WORKOUT_EXERCISE_HAS_SETS'
  | 'WORKOUT_EXERCISE_NOT_FOUND'
  | 'WORKOUT_HAS_NO_SETS'
  | 'WORKOUT_NOT_FOUND'
  | 'WORKOUT_NOT_IN_PROGRESS'
  // Next route handlers and proxy
  | 'BACKEND_UNREACHABLE'
  | 'INVALID_BACKEND_RESPONSE'
  | 'INVALID_ORIGIN'
  // Fallbacks when a body carries no code
  | 'UNKNOWN_ERROR'
  | 'ACTIVE_PROGRAM_FETCH_FAILED'
  | 'ACTIVE_WORKOUT_FETCH_FAILED'
  | 'USER_FETCH_FAILED'
  | 'EXERCISE_FETCH_FAILED'
  | 'EXERCISES_FETCH_FAILED'
  | 'MUSCLE_GROUPS_FETCH_FAILED'
  | 'PRESET_PROGRAM_FETCH_FAILED'
  | 'PRESET_PROGRAMS_FETCH_FAILED'
  | 'PRESET_ROUTINE_FETCH_FAILED'
  | 'PRESET_ROUTINES_FETCH_FAILED'
  | 'USER_PROGRAM_FETCH_FAILED'
  | 'USER_PROGRAMS_FETCH_FAILED'
  | 'USER_ROUTINE_FETCH_FAILED'
  | 'USER_ROUTINES_FETCH_FAILED';
