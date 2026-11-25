import { useRef, useState } from 'react';
import { toast } from 'sonner';

import { useErrorMessage } from '@/shared/api/errors/use-error-message';
import { deleteSet } from '../client/delete-set.client';
import { saveSet } from '../client/save-set.client';
import type { SetRow } from './set-rows';

//how long the spinner shows at least, a fast save would otherwise only flicker
const MIN_SAVING_MS = 800;

/** The rows of a workout per entry id, with saving and removing that keep the requests of one row in order. */
export function useSetRows(workoutId: number, initial: () => Record<number, SetRow[]>) {
  const [rows, setRows] = useState<Record<number, SetRow[]>>(initial);
  const errorMessage = useErrorMessage();
  const inFlight = useRef(new Map<string, Promise<unknown>>());

  //one request per row at a time: a new save waits for the previous one, so the server sees them in order
  function save(entryId: number, row: SetRow) {
    setRows((prev) => ({
      ...prev,
      [entryId]: prev[entryId].map((r) =>
        r.clientId === row.clientId ? { ...r, status: 'saving' as const } : r,
      ),
    }));

    const previous = inFlight.current.get(row.clientId) ?? Promise.resolve();
    const request = previous
      .catch(() => {})
      .then(() =>
        saveSet(workoutId, row.clientId, {
          workoutExerciseId: entryId,
          setNumber: row.setNumber,
          setType: row.setType,
          rpe: row.rpe,
          reps: row.reps,
          weightKg: row.weightKg,
          durationSeconds: row.durationSeconds,
          distanceMeters: row.distanceMeters,
        }),
      );
    inFlight.current.set(row.clientId, request);

    Promise.all([
      request.then(
        () => 'saved' as const,
        () => 'failed' as const,
      ),
      new Promise((resolve) => setTimeout(resolve, MIN_SAVING_MS)),
    ]).then(([status]) => {
      if (inFlight.current.get(row.clientId) !== request) return;
      inFlight.current.delete(row.clientId);
      setRows((prev) => ({
        ...prev,
        [entryId]: prev[entryId].map((r) =>
          r.clientId === row.clientId ? { ...r, status } : r,
        ),
      }));
    });
  }

  //an open row only exists here; a logged one is deleted on the server first, behind a save still running
  function remove(entryId: number, row: SetRow) {
    if (row.status === 'open') {
      setRows((prev) => ({
        ...prev,
        [entryId]: prev[entryId].filter((r) => r.clientId !== row.clientId),
      }));
      return;
    }

    const previous = inFlight.current.get(row.clientId) ?? Promise.resolve();
    const request = previous
      .catch(() => {})
      .then(() => deleteSet(workoutId, row.clientId));
    inFlight.current.set(row.clientId, request);

    request
      .then(
        () =>
          setRows((prev) => ({
            ...prev,
            [entryId]: prev[entryId].filter((r) => r.clientId !== row.clientId),
          })),
        (error) => toast.error(errorMessage(error)),
      )
      .finally(() => {
        if (inFlight.current.get(row.clientId) === request) {
          inFlight.current.delete(row.clientId);
        }
      });
  }

  //waits for the requests still running, e.g. before the workout closes; resolves to how many of them failed
  async function settle() {
    const results = await Promise.allSettled(inFlight.current.values());
    return results.filter((result) => result.status === 'rejected').length;
  }

  return { rows, setRows, save, remove, settle };
}
