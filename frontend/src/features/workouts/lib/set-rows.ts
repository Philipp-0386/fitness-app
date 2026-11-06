import type { TrackingType } from '@/features/exercises';
import type { WorkoutExercise, WorkoutSet } from '../types/api.types';
import { newClientId } from './client-id';

export type RowStatus = 'open' | 'saving' | 'saved' | 'failed';

/** A logged set, or an open row that only exists in the browser until it is logged. */
export type SetRow = WorkoutSet & { status: RowStatus };

/** The value fields of a set, which of them a row shows depends on the tracking type. */
export type SetField = 'weightKg' | 'reps' | 'durationSeconds' | 'distanceMeters';

/**
 * The rows of an entry: its logged sets, then open working rows until the target number of working sets is reached
 * (the routine's target, otherwise last time's, at least one). An open row takes the values of the same working set
 * last time, otherwise those of the row above, otherwise the targets.
 */
export function buildRows(entry: WorkoutExercise): SetRow[] {
  const rows: SetRow[] = entry.sets.map((set) => ({ ...set, status: 'saved' }));
  const lastWorking = entry.lastTime?.sets.filter((s) => s.setType === 'WORKING') ?? [];
  const target = Math.max(1, entry.targetSets ?? lastWorking.length);

  let working = rows.filter((row) => row.setType === 'WORKING').length;
  while (working < target) {
    const values = lastWorking[working] ??
      rows.at(-1) ?? {
        reps: entry.targetRepsMin,
        weightKg: null,
        durationSeconds: entry.targetDurationSeconds,
        distanceMeters: entry.targetDistanceMeters,
      };
    rows.push({
      clientId: newClientId(),
      setNumber: Math.max(0, ...rows.map((row) => row.setNumber)) + 1,
      setType: 'WORKING',
      reps: values.reps,
      weightKg: values.weightKg,
      durationSeconds: values.durationSeconds,
      distanceMeters: values.distanceMeters,
      status: 'open',
    });
    working++;
  }
  return rows;
}

/** Appends an open working row ("Add Set") with the values of the row above, or the targets if there is none. */
export function addRow(entry: WorkoutExercise, rows: SetRow[]): SetRow[] {
  const above = rows.at(-1);
  return [
    ...rows,
    {
      clientId: newClientId(),
      setNumber: Math.max(0, ...rows.map((row) => row.setNumber)) + 1,
      setType: 'WORKING',
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
