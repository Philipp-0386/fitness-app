'use client';

import { Check, LoaderCircle, Plus } from 'lucide-react';
import { useFormatter, useTranslations } from 'next-intl';
import { useRef, useState } from 'react';

import type { TrackingType } from '@/features/exercises';
import { cn } from '@/shared/lib/utils';
import { Button } from '@/shared/ui/button';
import { useFormatSetValues, weightHead } from '../lib/format-set';
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
  onOptionChange: (
    index: number,
    option: Partial<Pick<SetRow, 'setType' | 'rpe'>>,
  ) => void;
  onAdd: () => void;
  onLog?: (row: SetRow) => void;
  onRetry: (row: SetRow) => void;
  onRemove: (row: SetRow) => void;
};

export default function SetTable({
  entry,
  rows,
  onChange,
  onOptionChange,
  onAdd,
  onLog,
  onRetry,
  onRemove,
}: SetTableProps) {
  const [editing, setEditing] = useState<{ index: number; field: SetField } | null>(null);
  const [sheetId, setSheetId] = useState<string | null>(null);
  const cancelled = useRef(false);
  const t = useTranslations('workouts');
  const formatSetValues = useFormatSetValues();
  const format = useFormatter();

  const type = entry.exercise.trackingType;
  const heads: Record<SetField, string> = {
    weightKg: weightHead(type),
    reps: t('table.reps'),
    durationSeconds: t('table.time'),
    distanceMeters: t('table.meters'),
  };
  const active = rows.findIndex((row) => row.status === 'open');
  //the k-th row of a type is compared with the k-th set of that type last time
  const labels = rows.map((row, index) => {
    const number = rows
      .slice(0, index + 1)
      .filter((r) => r.setType === row.setType).length;
    const last = entry.lastTime?.sets.filter((s) => s.setType === row.setType)[
      number - 1
    ];
    return {
      last,
      short: row.setType === 'WORKING' ? String(number) : t(`setShort.${row.setType}`),
      title: t(`setTitle.${row.setType}`, { number }),
    };
  });
  const sheetIndex = rows.findIndex((row) => row.clientId === sheetId);

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
        <span>{t('table.set')}</span>
        <span>{t('table.lastTime')}</span>
        <span className="text-right">{heads[columns[type][0]]}</span>
        <span className="text-right">{heads[columns[type][1]]}</span>
        <span className="sr-only">{t('table.status')}</span>
      </div>

      <ol className="flex flex-col gap-1">
        {rows.map((row, index) => {
          const { last, short, title } = labels[index];

          return (
            <SwipeableRow
              key={row.clientId}
              actionLabel={t('table.remove')}
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
                aria-label={t('table.options', { title })}
                onClick={() => setSheetId(row.clientId)}
                className="flex min-h-tap flex-col items-start justify-center font-semibold"
              >
                {short}
                {row.rpe !== null && (
                  <span className="text-caption font-normal text-text-secondary">
                    @{format.number(row.rpe)}
                  </span>
                )}
              </button>
              {last ? (
                <span className="truncate text-meta text-text-secondary">
                  {formatSetValues(type, last, false)}
                </span>
              ) : (
                <span
                  aria-label={t('table.notDoneLastTime')}
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
                      : format.number(value, {
                          maximumFractionDigits: 2,
                          useGrouping: false,
                        });

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
                      onFocus={(e) => {
                        const end = e.currentTarget.value.length;
                        e.currentTarget.setSelectionRange(end, end);
                      }}
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
              <span className="flex justify-end">
                {onLog && index === active && (
                  <button
                    type="button"
                    disabled={requiredFields[type].some((field) => row[field] === null)}
                    onClick={() => onLog(row)}
                    aria-label={t('table.log', { title })}
                    className="group flex size-tap items-center justify-end disabled:opacity-40"
                  >
                    <span className="flex size-7 items-center justify-center rounded-full border border-action text-text-primary group-active:bg-surface-raised">
                      <Check className="size-4" aria-hidden="true" />
                    </span>
                  </button>
                )}
                {row.status === 'saving' && (
                  <LoaderCircle
                    className="size-5 animate-spin text-text-tertiary motion-reduce:animate-none"
                    aria-label={t('table.saving')}
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
                      aria-label={t('table.saved')}
                    />
                  </>
                )}
                {row.status === 'failed' && (
                  <button
                    type="button"
                    onClick={() => onRetry(row)}
                    className="min-h-tap text-meta font-semibold text-danger"
                  >
                    {t('table.retry')}
                  </button>
                )}
              </span>
            </SwipeableRow>
          );
        })}
      </ol>

      <Button type="button" variant="text" className="mt-2 w-full" onClick={onAdd}>
        <Plus aria-hidden="true" />
        {t('table.addSet')}
      </Button>

      {sheetIndex !== -1 && (
        <SetSheet
          title={labels[sheetIndex].title}
          setType={rows[sheetIndex].setType}
          rpe={rows[sheetIndex].rpe}
          onChange={(option) => onOptionChange(sheetIndex, option)}
          onRemove={() => {
            onRemove(rows[sheetIndex]);
            setSheetId(null);
          }}
          onClose={() => setSheetId(null)}
        />
      )}
    </div>
  );
}
