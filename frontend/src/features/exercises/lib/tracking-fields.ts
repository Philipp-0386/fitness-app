import { TrackingType } from '../types/api.types';

/** Which values a tracking type holds, mirrors TrackingType.holdsReps/holdsDuration/holdsDistance in the backend. */
export function trackingFields(type: TrackingType): {
  reps: boolean;
  duration: boolean;
  distance: boolean;
} {
  return {
    reps:
      type === 'WEIGHT_REPS' || type === 'BODYWEIGHT_REPS' || type === 'ASSISTED_REPS',
    duration: type === 'DURATION' || type === 'DISTANCE_DURATION',
    distance: type === 'WEIGHT_DISTANCE' || type === 'DISTANCE_DURATION',
  };
}
