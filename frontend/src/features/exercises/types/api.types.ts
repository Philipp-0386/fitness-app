export type ExerciseType = 'STRENGTH' | 'CARDIO' | 'MOBILITY';

/** Which values a logged set of the exercise holds. */
export type TrackingType =
  | 'WEIGHT_REPS'
  | 'BODYWEIGHT_REPS'
  | 'ASSISTED_REPS'
  | 'DURATION'
  | 'WEIGHT_DISTANCE'
  | 'DISTANCE_DURATION';

export type MuscleRole = 'PRIMARY' | 'SECONDARY';

/** Mirrors the backend ExerciseMuscleResponse record. */
export type ExerciseMuscle = {
  id: number;
  name: string;
  /** The muscle group itself, or the group an area like Upper Chest belongs to. */
  groupId: number;
  role: MuscleRole;
};

/** Mirrors the backend ExerciseResponse record. */
export type Exercise = {
  id: number;
  name: string;
  exerciseType: ExerciseType;
  trackingType: TrackingType;
  description: string | null;
  instructions: string | null;
  /** Primary muscles first. */
  muscles: ExerciseMuscle[];
  custom: boolean;
};

/** Mirrors the backend ExerciseRequest record. */
export type ExercisePayload = {
  name: string;
  exerciseType: ExerciseType;
  trackingType: TrackingType;
  description: string | null;
  instructions: string | null;
};
