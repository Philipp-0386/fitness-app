'use client';

import { Check, LoaderCircle, Plus } from 'lucide-react';
import { useRef, useState } from 'react';

import type { TrackingType } from '@/features/exercises';
import { cn } from '@/shared/lib/utils';
import { Button } from '@/shared/ui/button';
import { formatSetValues, weightHead } from '../lib/format-set';
import { requiredFields, type SetField, type SetRow } from '../lib/set-rows';
import type { WorkoutExercise } from '../types/api.types';
import SetSheet from './SetSheet';
import SwipeableRow from './SwipeableRow';

const columns: Record<TrackingType, [SetField, SetField]> = {
  WEIGHT_REPS: ['weightKg', 'reps'],
  BODYWEIGHT_REPS: ['weightKg', 'reps'],
  ASSISTED_REPS: ['weightKg', 'reps'],
  DURATION: ['durationSeconds', 'weightKg'],
  WEIGHT_DISTANCE: ['weightKg', 'distanceMeters'],
  DISTANCE_DURATION: ['distanceMeters', 'durationSeconds'],
};

const grid =
  'grid grid-cols-[2.5rem_minmax(0,1fr)_4.5rem_4.5rem_3rem] items-center gap-2 px-2';

type SetTableProps = {
  entry: WorkoutExercise;
  rows: SetRow[];
  onChange: (index: number, field: SetField, value: number | null) => void;
  onAdd: () => void;
  onRetry: (row: SetRow) => void;
  onRemove: (row: SetRow) => void;
};

export default function SetTable({
  entry,
  rows,
  onChange,
  onAdd,
  onRetry,
  onRemove,
}: SetTableProps) {
  const [editing, setEditing] = useState<{ index: number; field: SetField } | null>(null);
  const [sheet, setSheet] = useState<{ row: SetRow; title: string } | null>(null);
  const cancelled = useRef(false);

  const type = entry.exercise.trackingType;
  const heads: Record<SetField, string> = {
    weightKg: weightHead(type),
    reps: 'REPS',
    durationSeconds: 'TIME',
    distanceMeters: 'M',
  };
  const lastWorking = entry.lastTime?.sets.filter((s) => s.setType === 'WORKING') ?? [];
  const active = rows.findIndex((row) => row.status === 'open');

  function commit(index: number, field: SetField, text: string) {
    setEditing(null);
    if (cancelled.current) {
      cancelled.current = false;
      return;
    }
    const cleaned = text.trim().replace(',', '.').replace(/^[+-]/, '');
    if (cleaned === '') {
      //the backend would reject a logged set without it, so the old value stays
      if (rows[index].status !== 'open' && requiredFields[type].includes(field)) return;
      onChange(index, field, null);
      return;
    }
    //a time without separator is read like a microwave: the last two digits are the seconds, "130" is 1:30
    const [minutes, seconds] = cleaned.split(/[:.]/);
    const value =
      field !== 'durationSeconds'
        ? Number(cleaned)
        : seconds !== undefined
          ? Number(minutes) * 60 + Number(seconds)
          : Number(cleaned.slice(0, -2) || 0) * 60 + Number(cleaned.slice(-2));
    if (!Number.isFinite(value)) return;
    onChange(
      index,
      field,
      field === 'reps' || field === 'durationSeconds' ? Math.round(value) : value,
    );
  }

  return (
    <div className="mt-6">
      <div className={cn(grid, 'pb-2 text-meta font-semibold text-text-tertiary')}>
        <span>SET</span>
        <span>LAST TIME</span>
        <span className="text-right">{heads[columns[type][0]]}</span>
        <span className="text-right">{heads[columns[type][1]]}</span>
        <span className="sr-only">Status</span>
      </div>

      <ol className="flex flex-col gap-1">
        {rows.map((row, index) => {
          const workingNumber = rows
            .slice(0, index + 1)
            .filter((r) => r.setType === 'WORKING').length;
          const last =
            row.setType === 'WORKING' ? lastWorking[workingNumber - 1] : undefined;
          const title =
            row.setType === 'WORKING'
              ? `Set ${workingNumber}`
              : row.setType === 'WARMUP'
                ? 'Warm-Up'
                : 'Drop Set';

          return (
            <SwipeableRow
              key={row.clientId}
              actionLabel="Remove"
              onAction={() => onRemove(row)}
              className={cn(
                grid,
                'min-h-12 rounded-card tabular-nums',
                index === active &&
                  'min-h-14 border border-border-strong bg-surface text-[1.25rem] font-semibold',
                row.status === 'open' && index !== active && 'text-text-tertiary',
              )}
            >
              <button
                type="button"
                aria-haspopup="dialog"
                aria-label={`${title} options`}
                onClick={() => setSheet({ row, title })}
                className="min-h-tap text-left font-semibold"
              >
                {row.setType === 'WORKING'
                  ? workingNumber
                  : row.setType === 'WARMUP'
                    ? 'W'
                    : 'D'}
              </button>
              {last ? (
                <span className="truncate text-meta text-text-secondary">
                  {formatSetValues(type, last, false)}
                </span>
              ) : (
                <span
                  aria-label="Not done last time"
                  className="truncate text-meta text-text-secondary"
                >
                  -
                </span>
              )}
              {columns[type].map((field) => {
                const value = row[field];
                const text =
                  value === null
                    ? ''
                    : field === 'durationSeconds'
                      ? `${Math.floor(value / 60)}:${String(value % 60).padStart(2, '0')}`
                      : String(Number(value.toFixed(2)));

                if (editing?.index === index && editing.field === field) {
                  return (
                    <input
                      key={field}
                      autoFocus
                      inputMode={
                        field === 'reps' || field === 'durationSeconds'
                          ? 'numeric'
                          : 'decimal'
                      }
                      defaultValue={text}
                      aria-label={heads[field]}
                      onFocus={(e) => e.currentTarget.select()}
                      onBlur={(e) => commit(index, field, e.currentTarget.value)}
                      onKeyDown={(e) => {
                        if (e.key === 'Enter') e.currentTarget.blur();
                        if (e.key === 'Escape') {
                          cancelled.current = true;
                          e.currentTarget.blur();
                        }
                      }}
                      className="h-10 w-full min-w-0 rounded-field border border-border-strong bg-surface-raised px-2 text-right"
                    />
                  );
                }
                return (
                  <button
                    key={field}
                    type="button"
                    onClick={() => setEditing({ index, field })}
                    className="min-h-tap text-right"
                  >
                    {text || '-'}
                  </button>
                );
              })}
              <span className="flex justify-center">
                {row.status === 'saving' && (
                  <LoaderCircle
                    className="size-5 animate-spin text-text-tertiary motion-reduce:animate-none"
                    aria-label="Saving"
                  />
                )}
                {row.status === 'saved' && (
                  <>
                    <span
                      aria-hidden="true"
                      className="pointer-events-none absolute inset-0 -z-10 animate-saved-flash rounded-card bg-success-subtle"
                    />
                    <Check
                      className="size-5 animate-saved-pop text-success motion-reduce:animate-none"
                      aria-label="Saved"
                    />
                  </>
                )}
                {row.status === 'failed' && (
                  <button
                    type="button"
                    onClick={() => onRetry(row)}
                    className="min-h-tap text-meta font-semibold text-danger"
                  >
                    Retry
                  </button>
                )}
              </span>
            </SwipeableRow>
          );
        })}
      </ol>

      <Button type="button" variant="text" className="mt-2 w-full" onClick={onAdd}>
        <Plus aria-hidden="true" />
        Add Set
      </Button>

      {sheet && (
        <SetSheet
          title={sheet.title}
          onRemove={() => {
            onRemove(sheet.row);
            setSheet(null);
          }}
          onClose={() => setSheet(null)}
        />
      )}
    </div>
  );
}
