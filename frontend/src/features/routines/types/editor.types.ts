import { RoutineExerciseSummary } from './api.types';

export type Targets = {
  targetSets: number | null;
  targetRepsMin: number | null;
  targetRepsMax: number | null;
  targetDurationSeconds: number | null;
  targetDistanceMeters: number | null;
};

/** One row of the routine editor. The key is client side and never an exercise id, the same exercise may appear twice. */
export type Slot = Targets & {
  key: string;
  /** null while the slot is empty. */
  exercise: RoutineExerciseSummary | null;
};
