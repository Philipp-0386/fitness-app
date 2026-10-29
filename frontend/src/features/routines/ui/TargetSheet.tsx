'use client';

import { CircleAlert, Trash2 } from 'lucide-react';
import { useEffect, useRef, useState } from 'react';

import { trackingFields } from '@/features/exercises/lib/tracking-fields';
import { Button } from '@/shared/ui/button';
import { Input } from '@/shared/ui/input';
import { Label } from '@/shared/ui/label';
import { Sheet } from '@/shared/ui/sheet';
import { RoutineExerciseSummary } from '../types/api.types';
import { Targets } from '../types/editor.types';

type Draft = {
  sets: string;
  repsMin: string;
  repsMax: string;
  minutes: string;
  seconds: string;
  distance: string;
};

type TargetSheetProps = {
  slot: Targets;
  exercise: RoutineExerciseSummary;
  canMoveUp: boolean;
  canMoveDown: boolean;
  onDone: (targets: Targets) => void;
  onChangeExercise: () => void;
  onMoveUp: () => void;
  onMoveDown: () => void;
  onRemove: () => void;
  onClose: () => void;
};

/** null for an empty field, NaN for anything that is not a whole number. */
function parseWhole(value: string): number | null {
  const trimmed = value.trim();
  if (!trimmed) return null;
  return /^\d+$/.test(trimmed) ? Number(trimmed) : NaN;
}

//targets of one slot; the caller mounts it while open and keys it by slot, so the draft starts from the slot
export default function TargetSheet({
  slot,
  exercise,
  canMoveUp,
  canMoveDown,
  onDone,
  onChangeExercise,
  onMoveUp,
  onMoveDown,
  onRemove,
  onClose,
}: TargetSheetProps) {
  const dialogRef = useRef<HTMLDialogElement>(null);
  const inKm = exercise.trackingType === 'DISTANCE_DURATION';
  const fields = trackingFields(exercise.trackingType);
  const [draft, setDraft] = useState<Draft>(() => {
    const seconds = slot.targetDurationSeconds;
    const meters = slot.targetDistanceMeters;
    return {
      sets: slot.targetSets?.toString() ?? '',
      repsMin: slot.targetRepsMin?.toString() ?? '',
      repsMax: slot.targetRepsMax?.toString() ?? '',
      minutes: seconds === null ? '' : Math.floor(seconds / 60).toString(),
      seconds: seconds === null ? '' : (seconds % 60).toString(),
      distance: meters === null ? '' : (inKm ? meters / 1000 : meters).toString(),
    };
  });
  const [errors, setErrors] = useState<Partial<Record<keyof Draft, string>>>({});

  useEffect(() => {
    dialogRef.current?.showModal();
  }, []);

  function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    const found: Partial<Record<keyof Draft, string>> = {};
    const wholeError = 'Enter a whole number of at least 1.';

    const sets = parseWhole(draft.sets);
    if (sets !== null && !(sets >= 1)) found.sets = wholeError;

    const repsMin = fields.reps ? parseWhole(draft.repsMin) : null;
    const repsMax = fields.reps ? parseWhole(draft.repsMax) : null;
    if (repsMin !== null && !(repsMin >= 1)) found.repsMin = wholeError;
    if (repsMax !== null && !(repsMax >= 1)) found.repsMax = wholeError;
    if (
      !found.repsMin &&
      !found.repsMax &&
      repsMin !== null &&
      repsMax !== null &&
      repsMin > repsMax
    ) {
      found.repsMax = 'Must not be below the minimum.';
    }

    let duration: number | null = null;
    if (fields.duration) {
      const minutes = parseWhole(draft.minutes);
      const seconds = parseWhole(draft.seconds);
      if (minutes !== null && !(minutes >= 0)) found.minutes = 'Enter a whole number.';
      if (seconds !== null && !(seconds >= 0 && seconds <= 59))
        found.seconds = 'Enter 0 to 59.';
      if (!found.minutes && !found.seconds) {
        const total = (minutes ?? 0) * 60 + (seconds ?? 0);
        duration = total > 0 ? total : null;
      }
    }

    let distance: number | null = null;
    const distanceText = draft.distance.trim().replace(',', '.');
    if (fields.distance && distanceText) {
      const meters = /^\d+(\.\d+)?$/.test(distanceText)
        ? Math.round(Number(distanceText) * (inKm ? 1000 : 1) * 100) / 100
        : NaN;
      if (!(meters > 0)) found.distance = 'Enter a distance above 0.';
      else if (meters > 999999.99) found.distance = 'This distance is too long.';
      else distance = meters;
    }

    setErrors(found);
    if (Object.keys(found).length > 0) return;

    onDone({
      targetSets: sets,
      targetRepsMin: repsMin,
      targetRepsMax: repsMax,
      targetDurationSeconds: duration,
      targetDistanceMeters: distance,
    });
  }

  function field(
    name: keyof Draft,
    label: string,
    inputMode: 'numeric' | 'decimal' = 'numeric',
  ) {
    const id = `target-${name}`;
    const error = errors[name];
    return (
      <div>
        <Label className="mb-1.5" htmlFor={id}>
          {label}
        </Label>
        <Input
          id={id}
          type="text"
          inputMode={inputMode}
          autoComplete="off"
          value={draft[name]}
          onChange={(e) => setDraft({ ...draft, [name]: e.target.value })}
          aria-invalid={error ? true : undefined}
          aria-describedby={error ? `${id}-error` : undefined}
        />
        {error && (
          <p id={`${id}-error`} className="mt-1.5 flex gap-1.5 text-meta text-danger">
            <CircleAlert className="size-4 shrink-0 translate-y-0.5" aria-hidden="true" />
            {error}
          </p>
        )}
      </div>
    );
  }

  return (
    <Sheet ref={dialogRef} onClose={onClose} aria-labelledby="target-sheet-title">
      <form onSubmit={handleSubmit} noValidate>
        <div className="flex items-start justify-between gap-3">
          <h2 id="target-sheet-title" className="text-title">
            {exercise.name}
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
          {exercise.deleted
            ? 'No longer in the catalog, replace or remove it.'
            : 'Targets are optional.'}
        </p>

        <div className="grid grid-cols-3 items-start gap-3">
          {field('sets', 'Sets')}
          {fields.reps && field('repsMin', 'Reps min')}
          {fields.reps && field('repsMax', 'Reps max')}
          {fields.distance &&
            field('distance', inKm ? 'Distance (km)' : 'Distance (m)', 'decimal')}
          {fields.duration && field('minutes', 'Minutes')}
          {fields.duration && field('seconds', 'Seconds')}
        </div>

        <div className="mt-5 flex flex-col gap-2">
          <Button type="button" className="w-full" onClick={onChangeExercise}>
            Change exercise
          </Button>
          <div className="grid grid-cols-2 gap-2">
            <Button type="button" onClick={onMoveUp} disabled={!canMoveUp}>
              Move Up
            </Button>
            <Button type="button" onClick={onMoveDown} disabled={!canMoveDown}>
              Move Down
            </Button>
          </div>
          <Button type="submit" variant="primary" size="lg">
            Done
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
            Remove from routine
          </Button>
        </div>
      </form>
    </Sheet>
  );
}
