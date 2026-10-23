import { Targets } from '../types/editor.types';

/** Formats the targets as "3 x 8-12", "3 x 60 s" or "5 km, 30 min"; empty when none are set. */
export function formatTarget(targets: Targets): string {
  const values: string[] = [];

  if (targets.targetRepsMin !== null || targets.targetRepsMax !== null) {
    const min = targets.targetRepsMin ?? targets.targetRepsMax;
    const max = targets.targetRepsMax ?? targets.targetRepsMin;
    values.push(min === max ? `${min}` : `${min}-${max}`);
  }
  if (targets.targetDistanceMeters !== null) {
    const m = targets.targetDistanceMeters;
    values.push(m >= 1000 ? `${m / 1000} km` : `${m} m`);
  }
  if (targets.targetDurationSeconds !== null) {
    const s = targets.targetDurationSeconds;
    values.push(s % 60 === 0 ? `${s / 60} min` : `${s} s`);
  }

  const value = values.join(', ');
  if (targets.targetSets === null) return value;
  if (!value) return `${targets.targetSets} ${targets.targetSets === 1 ? 'set' : 'sets'}`;
  return `${targets.targetSets} x ${value}`;
}
