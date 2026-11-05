'use client';

import { useEffect, useRef } from 'react';

import { formatTarget } from '@/features/routines/lib/format-target';
import { cn } from '@/shared/lib/utils';
import { Button } from '@/shared/ui/button';
import { listRowClass } from '@/shared/ui/list';
import { Sheet } from '@/shared/ui/sheet';
import type { Workout } from '../types/api.types';

type OverviewSheetProps = {
  workout: Workout;
  current: number;
  onSelect: (index: number) => void;
  onClose: () => void;
};

export default function OverviewSheet({
  workout,
  current,
  onSelect,
  onClose,
}: OverviewSheetProps) {
  const dialogRef = useRef<HTMLDialogElement>(null);

  useEffect(() => {
    dialogRef.current?.showModal();
  }, []);

  const count = workout.exercises.length;

  return (
    <Sheet ref={dialogRef} onClose={onClose} aria-labelledby="overview-sheet-title">
      <div className="flex items-center justify-between gap-3">
        <h2 id="overview-sheet-title" className="truncate text-title">
          {workout.name}
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
      <p className="mt-1 mb-4 text-text-secondary">
        {count} {count === 1 ? 'exercise' : 'exercises'}
      </p>

      <ol className="divide-y divide-field-on-overlay">
        {workout.exercises.map((entry, index) => (
          <li key={entry.id}>
            <button
              type="button"
              onClick={() => onSelect(index)}
              aria-current={index === current ? 'step' : undefined}
              className={cn(
                listRowClass,
                'px-1 hover:bg-field-on-overlay active:bg-field-on-overlay',
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
                  {formatTarget(entry)}
                </span>
              </span>
            </button>
          </li>
        ))}
      </ol>
    </Sheet>
  );
}
