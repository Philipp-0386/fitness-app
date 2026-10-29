'use client';

import { ChevronLeft, ChevronRight, TriangleAlert } from 'lucide-react';
import { useEffect, useRef, useState } from 'react';

import { cn } from '@/shared/lib/utils';
import { Button } from '@/shared/ui/button';
import { listRowClass } from '@/shared/ui/list';
import { Sheet } from '@/shared/ui/sheet';
import { formatTarget } from '../lib/format-target';
import { RoutineDetail } from '../types/api.types';

type PresetSheetProps = {
  presets: RoutineDetail[];
  /** Whether using a preset overwrites something the user already entered. */
  replacesInput: boolean;
  onUse: (preset: RoutineDetail) => void;
  onClose: () => void;
};

//list of preset routines with a detail step; the caller mounts it while open
export default function PresetSheet({
  presets,
  replacesInput,
  onUse,
  onClose,
}: PresetSheetProps) {
  const dialogRef = useRef<HTMLDialogElement>(null);
  const [selected, setSelected] = useState<RoutineDetail | null>(null);

  useEffect(() => {
    dialogRef.current?.showModal();
  }, []);

  return (
    <Sheet ref={dialogRef} onClose={onClose} aria-labelledby="preset-sheet-title">
      <div className="flex items-center justify-between gap-3">
        {selected ? (
          <button
            type="button"
            onClick={() => setSelected(null)}
            className="-ml-2 flex min-h-tap items-center gap-0.5 pr-2 font-semibold text-text-secondary hover:text-text-primary"
          >
            <ChevronLeft className="size-6" aria-hidden="true" />
            Presets
          </button>
        ) : (
          <h2 id="preset-sheet-title" className="text-title">
            Presets
          </h2>
        )}
        <Button
          type="button"
          variant="text"
          size="sm"
          onClick={() => dialogRef.current?.close()}
        >
          Cancel
        </Button>
      </div>

      {selected ? (
        <>
          <h2 id="preset-sheet-title" className="mt-1 text-title">
            {selected.name}
          </h2>
          {selected.description && (
            <p className="mt-1 text-text-secondary">{selected.description}</p>
          )}
          <ul className="mt-3 divide-y divide-field-on-overlay">
            {selected.exercises.map((re) => (
              <li key={re.id} className="flex min-h-14 items-center gap-3 px-1 py-2.5">
                <span className="min-w-0 flex-1 font-semibold">{re.exercise.name}</span>
                <span className="shrink-0 text-meta text-text-secondary tabular-nums">
                  {formatTarget(re)}
                </span>
              </li>
            ))}
          </ul>
          {replacesInput && (
            <div className="mt-4 flex items-start gap-2.5 rounded-field bg-warning-subtle p-3 text-meta">
              <TriangleAlert
                className="size-5 shrink-0 text-warning"
                aria-hidden="true"
              />
              <span>Replaces the name and exercises you entered.</span>
            </div>
          )}
          <Button
            type="button"
            variant="primary"
            size="lg"
            className="mt-4"
            onClick={() => onUse(selected)}
          >
            Use Preset
          </Button>
        </>
      ) : (
        <>
          <p className="mt-1 mb-3 text-text-secondary">
            Start from a preset and adjust it to your training.
          </p>
          <ul className="divide-y divide-field-on-overlay">
            {presets.map((preset) => (
              <li key={preset.id}>
                <button
                  type="button"
                  onClick={() => setSelected(preset)}
                  className={cn(
                    listRowClass,
                    'px-1 hover:bg-field-on-overlay active:bg-field-on-overlay',
                  )}
                >
                  <span className="min-w-0 flex-1">
                    <span className="block truncate font-semibold">{preset.name}</span>
                    <span className="block text-meta text-text-secondary tabular-nums">
                      {preset.exercises.length}{' '}
                      {preset.exercises.length === 1 ? 'exercise' : 'exercises'}
                    </span>
                  </span>
                  <ChevronRight
                    className="size-5 shrink-0 text-text-tertiary"
                    aria-hidden="true"
                  />
                </button>
              </li>
            ))}
          </ul>
        </>
      )}
    </Sheet>
  );
}
