'use client';

import { ChevronLeft, ChevronRight, Ellipsis, Plus } from 'lucide-react';
import { useRouter } from 'next/navigation';
import { useRef, useState } from 'react';
import { toast } from 'sonner';

import type { Exercise, MuscleGroup } from '@/features/exercises';
import ExercisePicker from '@/features/exercises/ui/ExercisePicker';
import { formatTarget } from '@/features/routines/lib/format-target';
import { fallbackErrorMessage } from '@/shared/api/errors/fallback-error-message';
import { Button } from '@/shared/ui/button';
import { addExercise } from '../client/add-exercise.client';
import { deleteSet } from '../client/delete-set.client';
import { discardWorkout } from '../client/discard-workout.client';
import { finishWorkout } from '../client/finish-workout.client';
import { removeExercise } from '../client/remove-exercise.client';
import { reorderExercises } from '../client/reorder-exercises.client';
import { saveSet } from '../client/save-set.client';
import { swapExercise } from '../client/swap-exercise.client';
import type { Workout } from '../types/api.types';
import ExerciseSheet from './ExerciseSheet';
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

//how long the spinner shows at least, a fast save would otherwise only flicker
const MIN_SAVING_MS = 800;

type ActiveWorkoutViewProps = {
  workout: Workout;
  /** The exercises the picker offers. */
  catalog: Exercise[];
  muscleGroups: MuscleGroup[];
};

export default function ActiveWorkoutView({
  workout,
  catalog,
  muscleGroups,
}: ActiveWorkoutViewProps) {
  const [exercises, setExercises] = useState(workout.exercises);
  const [current, setCurrent] = useState(0);
  const [overviewOpen, setOverviewOpen] = useState(false);
  const [exerciseSheetOpen, setExerciseSheetOpen] = useState(false);
  const [finishOpen, setFinishOpen] = useState(false);
  const [picker, setPicker] = useState<'add' | 'swap' | null>(null);
  const [reordering, setReordering] = useState(false);
  const [reorderError, setReorderError] = useState<string | null>(null);
  const router = useRouter();
  const [rows, setRows] = useState<Record<number, SetRow[]>>(() =>
    Object.fromEntries(workout.exercises.map((e) => [e.id, buildRows(e)])),
  );

  const inFlight = useRef(new Map<string, Promise<unknown>>());

  const entry = exercises[current];
  const last = exercises.length - 1;
  const hasOpenRow =
    exercises.length > 0 && rows[entry.id].some((row) => row.status === 'open');
  const hasLogged =
    exercises.length > 0 && rows[entry.id].some((row) => row.status !== 'open');
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

  //the move shows at once; one request at a time, the buttons wait for it, and a failure restores the old order
  function moveExercise(index: number, delta: number) {
    const before = exercises;
    const beforeCurrent = current;
    const next = [...exercises];
    [next[index], next[index + delta]] = [next[index + delta], next[index]];
    setExercises(next);
    setCurrent(next.findIndex((e) => e.id === entry.id));
    setReordering(true);
    setReorderError(null);

    reorderExercises(
      workout.id,
      next.map((e) => e.id),
    )
      .catch((error) => {
        setExercises(before);
        setCurrent(beforeCurrent);
        setReorderError(fallbackErrorMessage(error));
      })
      .finally(() => setReordering(false));
  }

  async function handlePick(exercise: Exercise) {
    const kind = picker;
    setPicker(null);
    try {
      if (kind === 'swap') {
        const swapped = await swapExercise(workout.id, entry.id, exercise.id);
        setRows((prev) => ({ ...prev, [swapped.id]: buildRows(swapped) }));
        setExercises((prev) => prev.map((e) => (e.id === swapped.id ? swapped : e)));
      } else {
        const added = await addExercise(workout.id, exercise.id);
        setRows((prev) => ({ ...prev, [added.id]: buildRows(added) }));
        setExercises((prev) => [...prev, added]);
        setCurrent(exercises.length);
      }
    } catch (error) {
      toast.error(fallbackErrorMessage(error));
    }
  }

  async function handleRemoveExercise() {
    setExerciseSheetOpen(false);
    try {
      await removeExercise(workout.id, entry.id);
      setExercises((prev) => prev.filter((e) => e.id !== entry.id));
      setCurrent(Math.max(0, Math.min(current, exercises.length - 2)));
    } catch (error) {
      toast.error(fallbackErrorMessage(error));
    }
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
        total={exercises.length}
        onOpenOverview={() => setOverviewOpen(true)}
        onFinish={() => setFinishOpen(true)}
      />

      {exercises.length === 0 ? (
        <main className="flex-1 px-4 pt-6">
          <h1 className="text-title">No exercises yet</h1>
          <p className="mt-1 text-text-secondary">
            Add the exercises you want to train. Sets are saved as you log them.
          </p>
          <Button
            type="button"
            variant="primary"
            size="lg"
            className="mt-5"
            onClick={() => setPicker('add')}
          >
            <Plus aria-hidden="true" />
            Add Exercise
          </Button>
        </main>
      ) : (
        <>
          <main className="flex-1 px-4 pt-2">
            <div className="flex items-start justify-between gap-2">
              <h1 className="min-w-0 text-display text-balance">{entry.exercise.name}</h1>
              <Button
                type="button"
                variant="text"
                size="icon"
                aria-label="Exercise options"
                aria-haspopup="dialog"
                onClick={() => setExerciseSheetOpen(true)}
              >
                <Ellipsis aria-hidden="true" />
              </Button>
            </div>
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
          name={workout.name}
          exercises={exercises}
          rows={rows}
          current={current}
          reordering={reordering}
          error={reorderError}
          onSelect={(index) => {
            setCurrent(index);
            setOverviewOpen(false);
          }}
          onMove={moveExercise}
          onAdd={() => {
            setOverviewOpen(false);
            setPicker('add');
          }}
          onClose={() => {
            setOverviewOpen(false);
            setReorderError(null);
          }}
        />
      )}

      {exerciseSheetOpen && (
        <ExerciseSheet
          name={entry.exercise.name}
          hasLogged={hasLogged}
          onSwap={() => {
            setExerciseSheetOpen(false);
            setPicker('swap');
          }}
          onRemove={handleRemoveExercise}
          onClose={() => setExerciseSheetOpen(false)}
        />
      )}

      <ExercisePicker
        exercises={catalog}
        muscleGroups={muscleGroups}
        excludedIds={exercises.map((e) => e.exercise.id)}
        open={picker !== null}
        onSelect={handlePick}
        onClose={() => setPicker(null)}
      />
    </div>
  );
}
