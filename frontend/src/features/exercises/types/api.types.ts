export type ExerciseType = 'STRENGTH' | 'CARDIO' | 'MOBILITY';

/** Which values a logged set of the exercise holds. */
export type TrackingType =
  | 'WEIGHT_REPS'
  | 'BODYWEIGHT_REPS'
  | 'ASSISTED_REPS'
  | 'DURATION'
  | 'WEIGHT_DISTANCE'
  | 'DISTANCE_DURATION';

/** Mirrors the backend ExerciseResponse record. */
export type Exercise = {
  id: number;
  name: string;
  exerciseType: ExerciseType;
  trackingType: TrackingType;
  description: string | null;
  custom: boolean;
};
