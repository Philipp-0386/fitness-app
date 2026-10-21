import type { TrackingType } from '@/features/exercises';

// Mirrors backends RoutineResponse record.
export type Routine = {
  id: number;
  name: string;
  description: string | null;
  exerciseCount: number;
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
export type RoutineDetail = Omit<Routine, 'exerciseCount'> & {
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

/** Mirrors the backend UpdateRoutineRequest record. Describes the complete new state, omitted fields are cleared. */
export type UpdateRoutinePayload = {
  name: string;
  description?: string | null;
  exercises?: RoutineExercisePayload[];
};
