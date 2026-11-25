'use client';

import { arrayMove } from '@dnd-kit/sortable';
import { ChevronDown, ChevronUp } from 'lucide-react';
import { useRouter } from 'next/navigation';
import { useState } from 'react';

import { useFormatTarget } from '@/features/routines/lib/format-target';
import { fallbackErrorMessage } from '@/shared/api/errors/fallback-error-message';
import { Button } from '@/shared/ui/button';
import { reorderExercises } from '../client/reorder-exercises.client';
import { addRow, requiredFields, type SetRow } from '../lib/set-rows';
import { useSetRows } from '../lib/use-set-rows';
import type { Workout } from '../types/api.types';
import SetTable from './SetTable';

//a finished workout opened for corrections: every change is saved at once, like logging
export default function WorkoutEditView({ workout }: { workout: Workout }) {
  const router = useRouter();
  const formatTarget = useFormatTarget();
  const [exercises, setExercises] = useState(workout.exercises);
  const [reordering, setReordering] = useState(false);
  const [reorderError, setReorderError] = useState<string | null>(null);
  const [leaving, setLeaving] = useState(false);
  const [unsaved, setUnsaved] = useState<number | null>(null);
  const { rows, setRows, save, remove, settle } = useSetRows(workout.id, () =>
    Object.fromEntries(
      workout.exercises.map((e) => [
        e.id,
        e.sets.map((set): SetRow => ({ ...set, status: 'saved' })),
      ]),
    ),
  );

  //the move shows at once; one request at a time, moving waits for it, and a failure restores the old order
  function moveExercise(from: number, to: number) {
    const before = exercises;
    const next = arrayMove(exercises, from, to);
    setExercises(next);
    setReordering(true);
    setReorderError(null);

    reorderExercises(
      workout.id,
      next.map((e) => e.id),
    )
      .catch((error) => {
        setExercises(before);
        setReorderError(fallbackErrorMessage(error));
      })
      .finally(() => setReordering(false));
  }

  //the first tap with unsaved sets only reports them, the second leaves anyway
  async function done() {
    setLeaving(true);
    const failed =
      Object.values(rows)
        .flat()
        .filter((row) => row.status === 'failed').length + (await settle());
    if (failed > 0 && unsaved === null) {
      setUnsaved(failed);
      setLeaving(false);
      return;
    }
    router.push(`/history/${workout.id}`);
    router.refresh();
  }

  return (
    <div className="mx-auto flex w-full max-w-app flex-1 flex-col pb-8 lg:max-w-[560px]">
      <header className="sticky top-(--app-top-inset) z-10 flex h-14 items-center justify-between gap-3 border-b border-border-subtle bg-bg px-4">
        <p className="truncate text-[1.0625rem] font-semibold">{workout.name}</p>
        <Button
          type="button"
          variant="primary"
          size="sm"
          disabled={leaving}
          onClick={done}
        >
          Done
        </Button>
      </header>

      <h1 className="sr-only">Edit {workout.name}</h1>

      {unsaved !== null && (
        <p role="alert" className="mx-4 mt-4 text-meta text-danger">
          {unsaved === 1 ? '1 set was' : `${unsaved} sets were`} not saved. Retry them, or
          tap Done again to leave anyway.
        </p>
      )}
      {reorderError && (
        <p role="alert" className="mx-4 mt-4 text-meta text-danger">
          {reorderError}
        </p>
      )}

      {exercises.length === 0 && (
        <p className="mt-6 px-4 text-text-secondary">No exercises in this workout.</p>
      )}

      {exercises.map((entry, position) => {
        const type = entry.exercise.trackingType;
        const target = formatTarget(entry);
        const entryRows = rows[entry.id];
        return (
          <section
            key={entry.id}
            className="mx-4 mt-6"
            aria-labelledby={`entry-${entry.id}`}
          >
            <div className="flex items-start justify-between gap-2">
              <div className="min-w-0">
                <h2 id={`entry-${entry.id}`} className="text-title">
                  {entry.exercise.name}
                </h2>
                {target && (
                  <p className="mt-0.5 text-meta text-text-tertiary tabular-nums">
                    Target {target}
                  </p>
                )}
              </div>
              <div className="flex shrink-0">
                <Button
                  type="button"
                  variant="text"
                  size="icon"
                  aria-label={`Move ${entry.exercise.name} up`}
                  disabled={position === 0 || reordering}
                  onClick={() => moveExercise(position, position - 1)}
                >
                  <ChevronUp aria-hidden="true" />
                </Button>
                <Button
                  type="button"
                  variant="text"
                  size="icon"
                  aria-label={`Move ${entry.exercise.name} down`}
                  disabled={position === exercises.length - 1 || reordering}
                  onClick={() => moveExercise(position, position + 1)}
                >
                  <ChevronDown aria-hidden="true" />
                </Button>
              </div>
            </div>

            <SetTable
              entry={entry}
              rows={entryRows}
              onChange={(index, field, value) => {
                const next = entryRows.map((row, i) =>
                  i === index ? { ...row, [field]: value } : row,
                );
                setRows({ ...rows, [entry.id]: next });
                const row = next[index];
                if (
                  row.status !== 'open' ||
                  requiredFields[type].every((f) => row[f] !== null)
                ) {
                  save(entry.id, row);
                }
              }}
              onOptionChange={(index, option) => {
                const next = entryRows.map((row, i) =>
                  i === index ? { ...row, ...option } : row,
                );
                setRows({ ...rows, [entry.id]: next });
                const row = next[index];
                if (
                  row.status !== 'open' ||
                  requiredFields[type].every((f) => row[f] !== null)
                ) {
                  save(entry.id, row);
                }
              }}
              onAdd={() => {
                const next = addRow(entry, entryRows);
                setRows({ ...rows, [entry.id]: next });
                const row = next[next.length - 1];
                if (requiredFields[type].every((f) => row[f] !== null)) {
                  save(entry.id, row);
                }
              }}
              onRetry={(row) => save(entry.id, row)}
              onRemove={(row) => remove(entry.id, row)}
            />
          </section>
        );
      })}
    </div>
  );
}
