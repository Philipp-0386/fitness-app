import type { TrackingType } from '@/features/exercises';
import type { WorkoutSet } from '../types/api.types';

/** Column head of the weight: added weight for bodyweight, assistance for assisted exercises. */
export function weightHead(trackingType: TrackingType): string {
  if (trackingType === 'BODYWEIGHT_REPS') return '+kg';
  if (trackingType === 'ASSISTED_REPS') return '-kg';
  return 'kg';
}

/**
 * The values of a set as "80 kg x 10", "+5 kg x 10", "1:00" or "6 km, 38:00". Without unit the " kg" is left out,
 * for the narrow LAST TIME column ("80 x 10").
 */
export function formatSetValues(
  trackingType: TrackingType,
  set: Omit<WorkoutSet, 'clientId'>,
  withUnit = true,
): string {
  let weight: string | null = null;
  if (set.weightKg !== null) {
    const sign =
      set.weightKg > 0 && trackingType === 'BODYWEIGHT_REPS'
        ? '+'
        : set.weightKg > 0 && trackingType === 'ASSISTED_REPS'
          ? '-'
          : '';
    weight = `${sign}${Number(set.weightKg.toFixed(2))}${withUnit ? ' kg' : ''}`;
  }
  const duration =
    set.durationSeconds !== null
      ? `${Math.floor(set.durationSeconds / 60)}:${String(set.durationSeconds % 60).padStart(2, '0')}`
      : null;
  const distance =
    set.distanceMeters !== null
      ? set.distanceMeters >= 1000
        ? `${Number((set.distanceMeters / 1000).toFixed(2))} km`
        : `${set.distanceMeters} m`
      : null;

  switch (trackingType) {
    case 'DURATION':
      return [duration, weight].filter((part) => part !== null).join(', ');
    case 'WEIGHT_DISTANCE':
      return [weight, distance].filter((part) => part !== null).join(', ');
    case 'DISTANCE_DURATION':
      return [distance, duration].filter((part) => part !== null).join(', ');
    default:
      return weight !== null
        ? `${weight} x ${set.reps ?? '-'}`
        : `${set.reps ?? '-'} reps`;
  }
}
