import type { TrackingType } from '@/features/exercises';

// Mirrors backends RoutineResponse record.
export type Routine = {
  id: number;
  name: string;
  description: string | null;
  createdAt: string;
  updatedAt: string;
};

/** Mirrors the backend ExerciseSummary record. */
export type RoutineExerciseSummary = {
  id: number;
  name: string;
  trackingType: TrackingType;
  deleted: boolean;
};

/** Mirrors the backend RoutineExerciseResponse record. */
export type RoutineExercise = {
  id: number;
  orderIndex: number;
  exercise: RoutineExerciseSummary;
  targetSets: number | null;
  targetRepsMin: number | null;
  targetRepsMax: number | null;
  targetDurationSeconds: number | null;
  targetDistanceMeters: number | null;
};

/** Mirrors the backend RoutineDetailResponse record. */
export type RoutineDetail = Routine & {
  exercises: RoutineExercise[];
};

/** Mirrors the backend RoutineExerciseRequest record. The order of the list sets orderIndex. */
export type RoutineExercisePayload = {
  exerciseId: number;
  targetSets?: number | null;
  targetRepsMin?: number | null;
  targetRepsMax?: number | null;
  targetDurationSeconds?: number | null;
  targetDistanceMeters?: number | null;
};

/** Mirrors the backend CreateRoutineRequest record. */
export type CreateRoutinePayload = {
  name: string;
  description?: string | null;
  exercises?: RoutineExercisePayload[];
};
