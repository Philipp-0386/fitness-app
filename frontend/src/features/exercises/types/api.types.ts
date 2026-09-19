export type ExerciseType = 'STRENGTH' | 'CARDIO' | 'MOBILITY';

/** Mirrors the backend ExerciseResponse record. */
export type Exercise = {
  id: number;
  name: string;
  exerciseType: ExerciseType;
  description: string | null;
  custom: boolean;
};
