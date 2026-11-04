import type { TrackingType } from '@/features/exercises';

/** Mirrors the backend SetType enum. */
export type SetType = 'WARMUP' | 'WORKING' | 'DROP';

/** Mirrors the backend WorkoutSetResponse record. Which values are filled depends on the tracking type. */
export type WorkoutSet = {
  clientId: string;
  setNumber: number;
  setType: SetType;
  reps: number | null;
  weightKg: number | null;
  durationSeconds: number | null;
  distanceMeters: number | null;
};

/** Mirrors the backend LastTimeResponse record. */
export type LastTime = {
  startedAt: string;
  sets: WorkoutSet[];
};

/** Mirrors the backend ExerciseSummary record. */
export type WorkoutExerciseSummary = {
  id: number;
  name: string;
  trackingType: TrackingType;
  deleted: boolean;
};

/** Mirrors the backend WorkoutExerciseResponse record. The targets are snapshots taken at the start. */
export type WorkoutExercise = {
  id: number;
  orderIndex: number;
  exercise: WorkoutExerciseSummary;
  targetSets: number | null;
  targetRepsMin: number | null;
  targetRepsMax: number | null;
  targetDurationSeconds: number | null;
  targetDistanceMeters: number | null;
  sets: WorkoutSet[];
  lastTime: LastTime | null;
};

/** Mirrors the backend WorkoutResponse record. */
export type Workout = {
  id: number;
  name: string;
  routineId: number | null;
  programDayId: number | null;
  startedAt: string;
  exercises: WorkoutExercise[];
};

/** Mirrors the backend StartWorkoutRequest record. Exactly one of both is given. */
export type StartWorkoutPayload = { routineId: number } | { programDayId: number };
