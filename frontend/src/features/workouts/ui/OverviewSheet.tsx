'use client';

import { Check, ChevronDown, ChevronUp, Plus } from 'lucide-react';
import { useEffect, useRef } from 'react';

import { cn } from '@/shared/lib/utils';
import { Button } from '@/shared/ui/button';
import { listRowClass } from '@/shared/ui/list';
import { Sheet } from '@/shared/ui/sheet';
import type { SetRow } from '../lib/set-rows';
import type { WorkoutExercise } from '../types/api.types';

type OverviewSheetProps = {
  name: string;
  exercises: WorkoutExercise[];
  rows: Record<number, SetRow[]>;
  current: number;
  /** A new order is being saved, moving waits for it. */
  reordering: boolean;
  error: string | null;
  onSelect: (index: number) => void;
  onMove: (index: number, delta: number) => void;
  onAdd: () => void;
  onClose: () => void;
};

//all exercises of the workout; the caller mounts it while open
export default function OverviewSheet({
  name,
  exercises,
  rows,
  current,
  reordering,
  error,
  onSelect,
  onMove,
  onAdd,
  onClose,
}: OverviewSheetProps) {
  const dialogRef = useRef<HTMLDialogElement>(null);

  useEffect(() => {
    dialogRef.current?.showModal();
  }, []);

  const count = exercises.length;
  const logged = Object.values(rows)
    .flat()
    .filter((row) => row.status === 'saved' || row.status === 'saving').length;

  return (
    <Sheet ref={dialogRef} onClose={onClose} aria-labelledby="overview-sheet-title">
      <div className="flex items-center justify-between gap-3">
        <h2 id="overview-sheet-title" className="truncate text-title">
          {name}
        </h2>
        <Button
          type="button"
          variant="text"
          size="sm"
          onClick={() => dialogRef.current?.close()}
        >
          Close
        </Button>
      </div>
      <p className="mt-1 mb-4 text-text-secondary tabular-nums">
        {count} {count === 1 ? 'exercise' : 'exercises'}, {logged}{' '}
        {logged === 1 ? 'set' : 'sets'} logged
      </p>

      {error && (
        <p role="alert" className="mb-3 text-meta text-danger">
          {error}
        </p>
      )}

      <ol className="divide-y divide-field-on-overlay">
        {exercises.map((entry, index) => {
          const working = rows[entry.id].filter((row) => row.setType === 'WORKING');
          const done = working.filter(
            (row) => row.status === 'saved' || row.status === 'saving',
          ).length;
          const complete =
            rows[entry.id].length > 0 &&
            rows[entry.id].every((row) => row.status !== 'open');

          return (
            <li key={entry.id} className="flex items-center">
              <button
                type="button"
                onClick={() => onSelect(index)}
                aria-current={index === current ? 'step' : undefined}
                className={cn(
                  listRowClass,
                  'min-w-0 flex-1 px-1 hover:bg-field-on-overlay active:bg-field-on-overlay',
                  index === current && 'bg-field-on-overlay',
                )}
              >
                <span className="w-5 shrink-0 text-meta text-text-tertiary tabular-nums">
                  {index + 1}
                </span>
                <span className="min-w-0 flex-1">
                  <span className="block truncate font-semibold">
                    {entry.exercise.name}
                  </span>
                  <span className="block text-meta text-text-secondary tabular-nums">
                    {done} of {working.length} {working.length === 1 ? 'set' : 'sets'}
                  </span>
                </span>
                {complete && (
                  <Check className="size-5 shrink-0 text-success" aria-label="Done" />
                )}
              </button>
              <Button
                type="button"
                variant="text"
                size="icon"
                aria-label={`Move ${entry.exercise.name} up`}
                disabled={index === 0 || reordering}
                onClick={() => onMove(index, -1)}
              >
                <ChevronUp aria-hidden="true" />
              </Button>
              <Button
                type="button"
                variant="text"
                size="icon"
                aria-label={`Move ${entry.exercise.name} down`}
                disabled={index === count - 1 || reordering}
                onClick={() => onMove(index, 1)}
              >
                <ChevronDown aria-hidden="true" />
              </Button>
            </li>
          );
        })}
      </ol>

      <Button type="button" className="mt-4 w-full" onClick={onAdd}>
        <Plus aria-hidden="true" />
        Add Exercise
      </Button>
    </Sheet>
  );
}
