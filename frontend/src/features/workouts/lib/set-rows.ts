import type { TrackingType } from '@/features/exercises';
import type { SetType, WorkoutExercise, WorkoutSet } from '../types/api.types';
import { newClientId } from './client-id';

export type RowStatus = 'open' | 'saving' | 'saved' | 'failed';

/** A logged set, or an open row that only exists in the browser until it is logged. */
export type SetRow = WorkoutSet & { status: RowStatus };

/** The value fields of a set, which of them a row shows depends on the tracking type. */
export type SetField = 'weightKg' | 'reps' | 'durationSeconds' | 'distanceMeters';

/** The values a logged set must have per tracking type, the same rule as the backend's saveSet. */
export const requiredFields: Record<TrackingType, SetField[]> = {
  WEIGHT_REPS: ['weightKg', 'reps'],
  BODYWEIGHT_REPS: ['reps'],
  ASSISTED_REPS: ['reps'],
  DURATION: ['durationSeconds'],
  WEIGHT_DISTANCE: ['weightKg', 'distanceMeters'],
  DISTANCE_DURATION: ['distanceMeters', 'durationSeconds'],
};

/**
 * The rows of an entry: its logged sets, then open rows that follow last time's sets in their order (warm-ups,
 * working and drop sets) with as many working sets as the target asks for (the routine's target, otherwise last
 * time's, at least one). Missing working sets go after the last one, surplus ones are left out. The k-th set of a
 * type is open as long as fewer than k sets of that type are logged; warm-ups only until a working or drop set is
 * logged. An open row takes the values of its set last time, otherwise those of the working row above, otherwise the
 * targets.
 */
export function buildRows(entry: WorkoutExercise): SetRow[] {
  const rows: SetRow[] = entry.sets.map((set) => ({ ...set, status: 'saved' }));
  const lastSets = entry.lastTime?.sets ?? [];
  const target = Math.max(
    1,
    entry.targetSets ?? lastSets.filter((s) => s.setType === 'WORKING').length,
  );

  const plan: { setType: SetType; values: WorkoutSet | null }[] = [];
  let working = 0;
  for (const set of lastSets) {
    if (set.setType === 'WORKING') {
      if (working === target) continue;
      working++;
    }
    plan.push({ setType: set.setType, values: set });
  }
  let afterLastWorking = plan.length;
  for (let i = plan.length - 1; i >= 0; i--) {
    if (plan[i].setType === 'WORKING') {
      afterLastWorking = i + 1;
      break;
    }
  }
  plan.splice(
    afterLastWorking,
    0,
    ...Array.from({ length: target - working }, () => ({
      setType: 'WORKING' as const,
      values: null,
    })),
  );

  const logged: Record<SetType, number> = { WARMUP: 0, WORKING: 0, DROP: 0 };
  for (const row of rows) logged[row.setType]++;
  const warmedUp = logged.WORKING + logged.DROP > 0;
  const seen: Record<SetType, number> = { WARMUP: 0, WORKING: 0, DROP: 0 };
  for (const { setType, values } of plan) {
    seen[setType]++;
    if (seen[setType] <= logged[setType] || (setType === 'WARMUP' && warmedUp)) continue;
    const filled = values ??
      rows.findLast((row) => row.setType === 'WORKING') ?? {
        reps: entry.targetRepsMin,
        weightKg: null,
        durationSeconds: entry.targetDurationSeconds,
        distanceMeters: entry.targetDistanceMeters,
      };
    rows.push({
      clientId: newClientId(),
      setNumber: Math.max(0, ...rows.map((row) => row.setNumber)) + 1,
      setType,
      rpe: null,
      reps: filled.reps,
      weightKg: filled.weightKg,
      durationSeconds: filled.durationSeconds,
      distanceMeters: filled.distanceMeters,
      status: 'open',
    });
  }
  return rows;
}

/**
 * Appends an open working row ("Add Set") with the values of the last working row, otherwise the row above,
 * otherwise the targets.
 */
export function addRow(entry: WorkoutExercise, rows: SetRow[]): SetRow[] {
  const above = rows.findLast((row) => row.setType === 'WORKING') ?? rows.at(-1);
  return [
    ...rows,
    {
      clientId: newClientId(),
      setNumber: Math.max(0, ...rows.map((row) => row.setNumber)) + 1,
      setType: 'WORKING',
      rpe: null,
      reps: above ? above.reps : entry.targetRepsMin,
      weightKg: above ? above.weightKg : null,
      durationSeconds: above ? above.durationSeconds : entry.targetDurationSeconds,
      distanceMeters: above ? above.distanceMeters : entry.targetDistanceMeters,
      status: 'open',
    },
  ];
}

/**
 * After the weight of a working row went up: every later open working row with no or a lower weight takes it over.
 * Not for assisted exercises, where a higher weight means more help.
 */
export function carryWeightForward(
  rows: SetRow[],
  index: number,
  trackingType: TrackingType,
): SetRow[] {
  const weight = rows[index].weightKg;
  if (
    trackingType === 'ASSISTED_REPS' ||
    rows[index].setType !== 'WORKING' ||
    weight === null
  ) {
    return rows;
  }
  return rows.map((row, i) =>
    i > index &&
    row.status === 'open' &&
    row.setType === 'WORKING' &&
    (row.weightKg === null || row.weightKg < weight)
      ? { ...row, weightKg: weight }
      : row,
  );
}
