'use client';

import { ArrowLeftRight, Trash2 } from 'lucide-react';
import { useEffect, useRef } from 'react';

import { Button } from '@/shared/ui/button';
import { Sheet } from '@/shared/ui/sheet';

type ExerciseSheetProps = {
  name: string;
  /** The entry has a logged set, so it can neither be swapped nor removed. */
  hasLogged: boolean;
  onSwap: () => void;
  onRemove: () => void;
  onClose: () => void;
};

//options of the exercise on screen; the caller mounts it while open
export default function ExerciseSheet({
  name,
  hasLogged,
  onSwap,
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
