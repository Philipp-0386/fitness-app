'use client';

import { Trash2 } from 'lucide-react';
import { useEffect, useRef, useState } from 'react';

import { cn } from '@/shared/lib/utils';
import { Button } from '@/shared/ui/button';
import { Input } from '@/shared/ui/input';
import { Label } from '@/shared/ui/label';
import { Sheet } from '@/shared/ui/sheet';
import type { SetType } from '../types/api.types';

const typeOptions: { value: SetType; label: string }[] = [
  { value: 'WARMUP', label: 'Warm-Up' },
  { value: 'WORKING', label: 'Working' },
  { value: 'DROP', label: 'Drop' },
];

type SetSheetProps = {
  /** "Set 2", "Warm-Up" or "Drop Set". */
  title: string;
  setType: SetType;
  rpe: number | null;
  onChange: (option: { setType?: SetType; rpe?: number | null }) => void;
  onRemove: () => void;
  onClose: () => void;
};

//type, RPE and removal of one set; the caller mounts it while open
export default function SetSheet({
  title,
  setType,
  rpe,
  onChange,
  onRemove,
  onClose,
}: SetSheetProps) {
  const dialogRef = useRef<HTMLDialogElement>(null);
  const [draft, setDraft] = useState(rpe === null ? '' : String(rpe));
  const [rpeError, setRpeError] = useState<string | null>(null);

  useEffect(() => {
    dialogRef.current?.showModal();
  }, []);

  //runs on blur and again on close, since a tap on a button does not always blur the field on iOS
  function commitRpe() {
    const cleaned = draft.trim().replace(',', '.');
    const value = cleaned === '' ? null : Math.round(Number(cleaned) * 10) / 10;
    if (value !== null && !(value >= 1 && value <= 10)) {
      setRpeError('RPE goes from 1 to 10.');
      return;
    }
    setRpeError(null);
    if (value !== rpe) onChange({ rpe: value });
  }

  return (
    <Sheet
      ref={dialogRef}
      onClose={() => {
        commitRpe();
        onClose();
      }}
      aria-labelledby="set-sheet-title"
    >
      <div className="flex items-center justify-between gap-3">
        <h2 id="set-sheet-title" className="text-title">
          {title}
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

      <div
        role="radiogroup"
        aria-label="Set type"
        className="mt-4 grid grid-cols-3 gap-1 rounded-card bg-surface p-1"
      >
        {typeOptions.map((option) => (
          <button
            key={option.value}
            type="button"
            role="radio"
            aria-checked={setType === option.value}
            onClick={() =>
              setType !== option.value && onChange({ setType: option.value })
            }
            className={cn(
              'min-h-10 rounded-field text-meta font-semibold',
              setType === option.value
                ? 'bg-field-on-overlay text-text-primary'
                : 'text-text-tertiary hover:text-text-secondary',
            )}
          >
            {option.label}
          </button>
        ))}
      </div>

      <div className="mt-5 flex items-center justify-between gap-3">
        <Label htmlFor="set-rpe">RPE (optional)</Label>
        <Input
          id="set-rpe"
          inputMode="decimal"
          placeholder="1-10"
          value={draft}
          aria-invalid={rpeError !== null}
          aria-describedby={rpeError ? 'set-rpe-error' : undefined}
          onChange={(e) => setDraft(e.target.value)}
          onBlur={commitRpe}
          onKeyDown={(e) => {
            if (e.key === 'Enter') e.currentTarget.blur();
          }}
          className="w-24 text-right tabular-nums"
        />
      </div>
      {rpeError && (
        <p id="set-rpe-error" role="alert" className="mt-2 text-meta text-danger">
          {rpeError}
        </p>
      )}

      <div className="mt-5 border-t border-field-on-overlay pt-2">
        <Button
          type="button"
          variant="text"
          className="w-full text-danger hover:bg-field-on-overlay active:bg-field-on-overlay"
          onClick={onRemove}
        >
          <Trash2 aria-hidden="true" />
          Remove Set
        </Button>
      </div>
    </Sheet>
  );
}
