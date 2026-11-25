import { ExerciseType, TrackingType } from '../types/api.types';

// The backend accepts any combination, the allowed exercise types only guide the form.
export const trackingOptions: {
  value: TrackingType;
  exerciseTypes: ExerciseType[];
}[] = [
  { value: 'WEIGHT_REPS', exerciseTypes: ['STRENGTH'] },
  { value: 'BODYWEIGHT_REPS', exerciseTypes: ['STRENGTH', 'MOBILITY'] },
  { value: 'ASSISTED_REPS', exerciseTypes: ['STRENGTH'] },
  { value: 'DURATION', exerciseTypes: ['STRENGTH', 'CARDIO', 'MOBILITY'] },
  { value: 'WEIGHT_DISTANCE', exerciseTypes: ['STRENGTH'] },
  { value: 'DISTANCE_DURATION', exerciseTypes: ['CARDIO'] },
];
