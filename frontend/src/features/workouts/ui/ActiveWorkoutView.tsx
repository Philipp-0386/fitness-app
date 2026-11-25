'use client';

import { arrayMove } from '@dnd-kit/sortable';
import { ChevronLeft, ChevronRight, Ellipsis, Plus } from 'lucide-react';
import { useRouter } from 'next/navigation';
import { useTranslations } from 'next-intl';
import { useState } from 'react';
import { toast } from 'sonner';

import type { Exercise, MuscleGroup } from '@/features/exercises';
import ExercisePicker from '@/features/exercises/ui/ExercisePicker';
import { useFormatTarget } from '@/features/routines/lib/format-target';
import { useErrorMessage } from '@/shared/api/errors/use-error-message';
import { Button } from '@/shared/ui/button';
import { addExercise } from '../client/add-exercise.client';
import { discardWorkout } from '../client/discard-workout.client';
import { finishWorkout } from '../client/finish-workout.client';
import { removeExercise } from '../client/remove-exercise.client';
import { reorderExercises } from '../client/reorder-exercises.client';
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
import { useFormatDaysAgo } from '../lib/format-days-ago';
import { useSetRows } from '../lib/use-set-rows';
import SetTable from './SetTable';

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
  const formatTarget = useFormatTarget();
  const formatDaysAgo = useFormatDaysAgo();
  const t = useTranslations('workouts');
  const errorMessage = useErrorMessage();
  const [exercises, setExercises] = useState(workout.exercises);
  const [current, setCurrent] = useState(0);
  const [overviewOpen, setOverviewOpen] = useState(false);
  const [exerciseSheetOpen, setExerciseSheetOpen] = useState(false);
  const [finishOpen, setFinishOpen] = useState(false);
  const [picker, setPicker] = useState<'add' | 'swap' | null>(null);
  const [reordering, setReordering] = useState(false);
  const [reorderError, setReorderError] = useState<string | null>(null);
  const router = useRouter();
  const { rows, setRows, save, remove, settle } = useSetRows(workout.id, () =>
    Object.fromEntries(workout.exercises.map((e) => [e.id, buildRows(e)])),
  );

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
    await settle();
    await finishWorkout(workout.id);
    router.push(`/workout/${workout.id}/summary`);
    router.refresh();
  }

  async function discard() {
    await discardWorkout(workout.id);
    router.push('/dashboard');
    router.refresh();
  }

  //the move shows at once; one request at a time, moving waits for it, and a failure restores the old order
  function moveExercise(from: number, to: number) {
    const before = exercises;
    const beforeCurrent = current;
    const next = arrayMove(exercises, from, to);
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
        setReorderError(errorMessage(error));
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
      toast.error(errorMessage(error));
    }
  }

  async function handleRemoveExercise() {
    setExerciseSheetOpen(false);
    try {
      await removeExercise(workout.id, entry.id);
      setExercises((prev) => prev.filter((e) => e.id !== entry.id));
      setCurrent(Math.max(0, Math.min(current, exercises.length - 2)));
    } catch (error) {
      toast.error(errorMessage(error));
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

  function handleOptionChange(
    index: number,
    option: Partial<Pick<SetRow, 'setType' | 'rpe'>>,
  ) {
    const next = rows[entry.id].map((row, i) =>
      i === index ? { ...row, ...option } : row,
    );
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
          <h1 className="text-title">{t('empty')}</h1>
          <p className="mt-1 text-text-secondary">{t('emptyText')}</p>
          <Button
            type="button"
            variant="primary"
            size="lg"
            className="mt-5"
            onClick={() => setPicker('add')}
          >
            <Plus aria-hidden="true" />
            {t('addExercise')}
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
                aria-label={t('exerciseOptions')}
                aria-haspopup="dialog"
                onClick={() => setExerciseSheetOpen(true)}
              >
                <Ellipsis aria-hidden="true" />
              </Button>
            </div>
            <p className="mt-1 flex items-baseline justify-between gap-3 text-text-secondary tabular-nums">
              <span>{formatTarget(entry)}</span>
              {entry.lastTime && (
                <span suppressHydrationWarning className="text-meta text-text-tertiary">
                  {t('lastTime', { when: formatDaysAgo(entry.lastTime.startedAt) })}
                </span>
              )}
            </p>
            <SetTable
              entry={entry}
              rows={rows[entry.id]}
              onChange={handleChange}
              onOptionChange={handleOptionChange}
              onAdd={() =>
                setRows({ ...rows, [entry.id]: addRow(entry, rows[entry.id]) })
              }
              onLog={(row) => save(entry.id, row)}
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
                {t('previous')}
              </Button>
              {hasOpenRow && (
                <Button
                  type="button"
                  onClick={() => setCurrent(current + 1)}
                  disabled={current === last}
                >
                  {t('next')}
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
          position={current + 1}
          total={exercises.length}
          canMoveUp={current > 0 && !reordering}
          canMoveDown={current < exercises.length - 1 && !reordering}
          error={reorderError}
          onSwap={() => {
            setExerciseSheetOpen(false);
            setPicker('swap');
          }}
          onMoveUp={() => moveExercise(current, current - 1)}
          onMoveDown={() => moveExercise(current, current + 1)}
          onRemove={handleRemoveExercise}
          onClose={() => {
            setExerciseSheetOpen(false);
            setReorderError(null);
          }}
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
