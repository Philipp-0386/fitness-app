import { ExerciseType, TrackingType } from '../types/api.types';

// The backend accepts any combination, the allowed exercise types only guide the form.
export const trackingOptions: {
  value: TrackingType;
  label: string;
  hint: string;
  exerciseTypes: ExerciseType[];
}[] = [
  {
    value: 'WEIGHT_REPS',
    label: 'Weight and reps',
    hint: 'e.g. bench press',
    exerciseTypes: ['STRENGTH'],
  },
  {
    value: 'BODYWEIGHT_REPS',
    label: 'Bodyweight reps',
    hint: 'Optional added weight, e.g. pull-up',
    exerciseTypes: ['STRENGTH', 'MOBILITY'],
  },
  {
    value: 'ASSISTED_REPS',
    label: 'Assisted reps',
    hint: 'Weight is the assistance, e.g. assisted dip',
    exerciseTypes: ['STRENGTH'],
  },
  {
    value: 'DURATION',
    label: 'Time',
    hint: 'e.g. plank, stretch',
    exerciseTypes: ['STRENGTH', 'CARDIO', 'MOBILITY'],
  },
  {
    value: 'WEIGHT_DISTANCE',
    label: 'Weight and distance',
    hint: "e.g. farmer's carry",
    exerciseTypes: ['STRENGTH'],
  },
  {
    value: 'DISTANCE_DURATION',
    label: 'Distance and time',
    hint: 'e.g. running, rowing',
    exerciseTypes: ['CARDIO'],
  },
];
