'use client';

import { ArrowLeftRight, Trash2 } from 'lucide-react';
import { useEffect, useRef } from 'react';

import { Button } from '@/shared/ui/button';
import { Sheet } from '@/shared/ui/sheet';

type ExerciseSheetProps = {
  name: string;
  /** The entry has a logged set, so it can neither be swapped nor removed. */
  hasLogged: boolean;
  /** Place of the exercise in the workout, counted from 1. */
  position: number;
  total: number;
  canMoveUp: boolean;
  canMoveDown: boolean;
  error: string | null;
  onSwap: () => void;
  onMoveUp: () => void;
  onMoveDown: () => void;
  onRemove: () => void;
  onClose: () => void;
};

//options of the exercise on screen; the caller mounts it while open
export default function ExerciseSheet({
  name,
  hasLogged,
  position,
  total,
  canMoveUp,
  canMoveDown,
  error,
  onSwap,
  onMoveUp,
  onMoveDown,
  onRemove,
  onClose,
}: ExerciseSheetProps) {
  const dialogRef = useRef<HTMLDialogElement>(null);

  useEffect(() => {
    dialogRef.current?.showModal();
  }, []);

  return (
    <Sheet ref={dialogRef} onClose={onClose} aria-labelledby="exercise-sheet-title">
      <div className="flex items-center justify-between gap-3">
        <h2 id="exercise-sheet-title" className="min-w-0 truncate text-title">
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

      {error && (
        <p role="alert" className="mt-3 text-meta text-danger">
          {error}
        </p>
      )}

      <div className="mt-4">
        <Button type="button" className="w-full" disabled={hasLogged} onClick={onSwap}>
          <ArrowLeftRight aria-hidden="true" />
          Swap Exercise
        </Button>
        {hasLogged && (
          <p className="mt-2 text-meta text-text-secondary">
            Has logged sets. Add the other exercise instead.
          </p>
        )}
      </div>

      <div className="mt-2 grid grid-cols-[1fr_auto_1fr] items-center gap-2">
        <Button type="button" onClick={onMoveUp} disabled={!canMoveUp}>
          Move Up
        </Button>
        <span
          aria-live="polite"
          className="px-1 font-semibold whitespace-nowrap text-text-secondary tabular-nums"
        >
          {position} of {total}
        </span>
        <Button type="button" onClick={onMoveDown} disabled={!canMoveDown}>
          Move Down
        </Button>
      </div>

      {!hasLogged && (
        <div className="mt-4 border-t border-field-on-overlay pt-2">
          <Button
            type="button"
            variant="text"
            className="w-full text-danger hover:bg-field-on-overlay active:bg-field-on-overlay"
            onClick={onRemove}
          >
            <Trash2 aria-hidden="true" />
            Remove Exercise
          </Button>
        </div>
      )}
    </Sheet>
  );
}
