'use client';

import { ChevronLeft, ChevronRight } from 'lucide-react';
import { useRouter } from 'next/navigation';
import { useRef, useState } from 'react';
import { toast } from 'sonner';

import { formatTarget } from '@/features/routines/lib/format-target';
import { fallbackErrorMessage } from '@/shared/api/errors/fallback-error-message';
import { Button } from '@/shared/ui/button';
import { deleteSet } from '../client/delete-set.client';
import { discardWorkout } from '../client/discard-workout.client';
import { finishWorkout } from '../client/finish-workout.client';
import { saveSet } from '../client/save-set.client';
import type { Workout } from '../types/api.types';
import FinishSheet from './FinishSheet';
import LogBar from './LogBar';
import OverviewSheet from './OverviewSheet';
import WorkoutTopBar from './WorkoutTopBar';
import {
  addRow,
  buildRows,
  carryWeightForward,
  type SetField,
  type SetRow,
} from '../lib/set-rows';
import SetTable from './SetTable';

type ActiveWorkoutViewProps = {
  workout: Workout;
};

export default function ActiveWorkoutView({ workout }: ActiveWorkoutViewProps) {
  const [current, setCurrent] = useState(0);
  const [overviewOpen, setOverviewOpen] = useState(false);
  const [finishOpen, setFinishOpen] = useState(false);
  const router = useRouter();
  const [rows, setRows] = useState<Record<number, SetRow[]>>(() =>
    Object.fromEntries(workout.exercises.map((e) => [e.id, buildRows(e)])),
  );

  const inFlight = useRef(new Map<string, Promise<unknown>>());

  const entry = workout.exercises[current];
  const last = workout.exercises.length - 1;
  const hasOpenRow =
    workout.exercises.length > 0 && rows[entry.id].some((row) => row.status === 'open');
  const allRows = Object.values(rows).flat();
  const loggedCount = allRows.filter(
    (row) => row.status === 'saved' || row.status === 'saving',
  ).length;
  const failedCount = allRows.filter((row) => row.status === 'failed').length;

  //waits for the saves still running, so the newest values are stored before the workout closes
  async function finish() {
    await Promise.allSettled(inFlight.current.values());
    await finishWorkout(workout.id);
    router.push('/dashboard');
    router.refresh();
  }

  async function discard() {
    await discardWorkout(workout.id);
    router.push('/dashboard');
    router.refresh();
  }

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
        saveSet(workout.id, row.clientId, {
          workoutExerciseId: entryId,
          setNumber: row.setNumber,
          setType: row.setType,
          reps: row.reps,
          weightKg: row.weightKg,
          durationSeconds: row.durationSeconds,
          distanceMeters: row.distanceMeters,
        }),
      );
    inFlight.current.set(row.clientId, request);

    request
      .then(
        () => 'saved' as const,
        () => 'failed' as const,
      )
      .then((status) => {
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
      .then(() => deleteSet(workout.id, row.clientId));
    inFlight.current.set(row.clientId, request);

    request
      .then(
        () =>
          setRows((prev) => ({
            ...prev,
            [entryId]: prev[entryId].filter((r) => r.clientId !== row.clientId),
          })),
        (error) => toast.error(fallbackErrorMessage(error)),
      )
      .finally(() => {
        if (inFlight.current.get(row.clientId) === request) {
          inFlight.current.delete(row.clientId);
        }
      });
  }

  function handleChange(index: number, field: SetField, value: number | null) {
    const before = rows[entry.id];
    const oldWeight = before[index].weightKg;
    let next = before.map((row, i) => (i === index ? { ...row, [field]: value } : row));
    if (
      field === 'weightKg' &&
      value !== null &&
      (oldWeight === null || value > oldWeight)
    ) {
      next = carryWeightForward(next, index, entry.exercise.trackingType);
    }
    setRows({ ...rows, [entry.id]: next });
    if (next[index].status !== 'open') save(entry.id, next[index]);
  }

  return (
    <div className="mx-auto flex w-full max-w-app flex-1 flex-col">
      <WorkoutTopBar
        name={workout.name}
        startedAt={workout.startedAt}
        current={current}
        total={workout.exercises.length}
        onOpenOverview={() => setOverviewOpen(true)}
        onFinish={() => setFinishOpen(true)}
      />

      {workout.exercises.length === 0 ? (
        <main className="flex-1 px-4 pt-2">
          <p className="text-text-secondary">No exercises yet.</p>
        </main>
      ) : (
        <>
          <main className="flex-1 px-4 pt-2">
            <h1 className="text-display text-balance">{entry.exercise.name}</h1>
            <p className="mt-1 text-text-secondary tabular-nums">{formatTarget(entry)}</p>
            <SetTable
              entry={entry}
              rows={rows[entry.id]}
              onChange={handleChange}
              onAdd={() =>
                setRows({ ...rows, [entry.id]: addRow(entry, rows[entry.id]) })
              }
              onRetry={(row) => save(entry.id, row)}
              onRemove={(row) => remove(entry.id, row)}
            />
          </main>

          <div className="sticky bottom-0 flex flex-col gap-3 bg-bg px-4 pt-3 pb-[calc(1rem+env(safe-area-inset-bottom))]">
            <div className="grid grid-cols-2 gap-3">
              <Button
                type="button"
                onClick={() => setCurrent(current - 1)}
                disabled={current === 0}
              >
                <ChevronLeft aria-hidden="true" />
                Previous
              </Button>
              {hasOpenRow && (
                <Button
                  type="button"
                  onClick={() => setCurrent(current + 1)}
                  disabled={current === last}
                >
                  Next
                  <ChevronRight aria-hidden="true" />
                </Button>
              )}
            </div>
            <LogBar
              entry={entry}
              rows={rows[entry.id]}
              isLast={current === last}
              onLog={(row) => save(entry.id, row)}
              onNext={() => setCurrent(current + 1)}
              onFinish={() => setFinishOpen(true)}
            />
          </div>
        </>
      )}

      {finishOpen && (
        <FinishSheet
          startedAt={workout.startedAt}
          loggedCount={loggedCount}
          failedCount={failedCount}
          onFinish={finish}
          onDiscard={discard}
          onClose={() => setFinishOpen(false)}
        />
      )}

      {overviewOpen && (
        <OverviewSheet
          workout={workout}
          current={current}
          onSelect={(index) => {
            setCurrent(index);
            setOverviewOpen(false);
          }}
          onClose={() => setOverviewOpen(false)}
        />
      )}
    </div>
  );
}
