'use client';

import { Check, Trash2 } from 'lucide-react';
import { useEffect, useRef } from 'react';

import type { Routine } from '@/features/routines';
import { cn } from '@/shared/lib/utils';
import { Button } from '@/shared/ui/button';
import { listRowClass } from '@/shared/ui/list';
import { Sheet } from '@/shared/ui/sheet';
import { DayDraft } from '../types/editor.types';

type DaySheetProps = {
  /** null while adding a new day. */
  day: DayDraft | null;
  dayNumber: number;
  routines: Routine[];
  canMoveUp: boolean;
  canMoveDown: boolean;
  onSelect: (routine: Routine | null) => void;
  onMoveUp: () => void;
  onMoveDown: () => void;
  onRemove: () => void;
  onClose: () => void;
};

//content of one day; the caller mounts it while open and keys it by day
export default function DaySheet({
  day,
  dayNumber,
  routines,
  canMoveUp,
  canMoveDown,
  onSelect,
  onMoveUp,
  onMoveDown,
  onRemove,
  onClose,
}: DaySheetProps) {
  const dialogRef = useRef<HTMLDialogElement>(null);

  useEffect(() => {
    dialogRef.current?.showModal();
  }, []);

  const options = [
    { key: 'rest', routine: null, label: 'Rest day', meta: 'No training' },
    ...routines.map((routine) => ({
      key: `routine-${routine.id}`,
      routine,
      label: routine.name,
      meta: `${routine.exerciseCount} ${routine.exerciseCount === 1 ? 'exercise' : 'exercises'}`,
    })),
  ];

  return (
    <Sheet ref={dialogRef} onClose={onClose} aria-labelledby="day-sheet-title">
      <div className="flex items-center justify-between gap-3">
        <h2 id="day-sheet-title" className="text-title">
          {day ? `Day ${dayNumber}` : 'Add Day'}
        </h2>
        <Button
          type="button"
          variant="text"
          size="sm"
          onClick={() => dialogRef.current?.close()}
        >
          Cancel
        </Button>
      </div>
      <p className="mt-1 mb-4 text-text-secondary">
        {routines.length === 0
          ? 'You have no routines yet. Create one under Plans first.'
          : 'Pick a routine or a rest day.'}
      </p>

      <ul className="divide-y divide-field-on-overlay">
        {options.map((option) => {
          const selected = day !== null && day.routine?.id === option.routine?.id;
          return (
            <li key={option.key}>
              <button
                type="button"
                onClick={() => onSelect(option.routine)}
                aria-pressed={day ? selected : undefined}
                className={cn(
                  listRowClass,
                  'px-1 hover:bg-field-on-overlay active:bg-field-on-overlay',
                )}
              >
                <span className="min-w-0 flex-1">
                  <span className="block truncate font-semibold">{option.label}</span>
                  <span className="block text-meta text-text-secondary tabular-nums">
                    {option.meta}
                  </span>
                </span>
                {selected && <Check className="size-5 shrink-0" aria-hidden="true" />}
              </button>
            </li>
          );
        })}
      </ul>

      {day && (
        <>
          <div className="mt-5 grid grid-cols-2 gap-2">
            <Button type="button" onClick={onMoveUp} disabled={!canMoveUp}>
              Move Up
            </Button>
            <Button type="button" onClick={onMoveDown} disabled={!canMoveDown}>
              Move Down
            </Button>
          </div>

          <div className="mt-4 border-t border-field-on-overlay pt-2">
            <Button
              type="button"
              variant="text"
              className="w-full text-danger hover:bg-field-on-overlay active:bg-field-on-overlay"
              onClick={onRemove}
            >
              <Trash2 aria-hidden="true" />
              Remove Day
            </Button>
          </div>
        </>
      )}
    </Sheet>
  );
}
