'use client';

import {
  DndContext,
  DragEndEvent,
  KeyboardSensor,
  PointerSensor,
  closestCenter,
  useSensor,
  useSensors,
} from '@dnd-kit/core';
import {
  SortableContext,
  arrayMove,
  sortableKeyboardCoordinates,
  useSortable,
  verticalListSortingStrategy,
} from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';
import { CircleAlert, GripVertical, Plus, Trash2 } from 'lucide-react';
import { useRouter } from 'next/navigation';
import { useRef, useState } from 'react';
import { toast } from 'sonner';

import type { Routine } from '@/features/routines';
import { ApiError } from '@/shared/api/errors/api-error';
import { fallbackErrorMessage } from '@/shared/api/errors/fallback-error-message';
import { cn } from '@/shared/lib/utils';
import { Button } from '@/shared/ui/button';
import { Input } from '@/shared/ui/input';
import { Label } from '@/shared/ui/label';
import { listClass, listRowClass } from '@/shared/ui/list';
import { createProgram } from '../client/create-program.client';
import { DayDraft } from '../types/editor.types';
import DaySheet from './DaySheet';

const MAX_DAYS = 14;

const cancelClass =
  '-ml-2 flex min-h-tap items-center justify-self-start px-2 font-semibold text-text-secondary hover:text-text-primary';

/** dayKey is null while a new day is added. */
type OpenSheet = { dayKey: string | null } | null;

type ProgramEditorProps = {
  routines: Routine[];
};

export default function ProgramEditor({ routines }: ProgramEditorProps) {
  const router = useRouter();
  const addedDays = useRef(0);
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [days, setDays] = useState<DayDraft[]>([]);
  const [sheet, setSheet] = useState<OpenSheet>(null);
  const [nameError, setNameError] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [isSaving, setIsSaving] = useState(false);

  const sensors = useSensors(
    useSensor(PointerSensor),
    useSensor(KeyboardSensor, { coordinateGetter: sortableKeyboardCoordinates }),
  );

  const sheetIndex = sheet?.dayKey ? days.findIndex((d) => d.key === sheet.dayKey) : -1;
  const sheetDay = sheetIndex >= 0 ? days[sheetIndex] : null;

  function handleSelect(routine: Routine | null) {
    if (sheetDay) {
      setDays(days.map((d) => (d.key === sheetDay.key ? { ...d, routine } : d)));
    } else {
      setDays([...days, { key: `added-${++addedDays.current}`, routine }]);
    }
    setSheet(null);
  }

  function handleDragEnd({ active, over }: DragEndEvent) {
    if (!over || active.id === over.id) return;
    const from = days.findIndex((d) => d.key === active.id);
    const to = days.findIndex((d) => d.key === over.id);
    setDays(arrayMove(days, from, to));
  }

  async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    if (isSaving) return;
    setNameError(null);
    setError(null);

    if (!name.trim()) {
      setNameError('Please enter a name.');
      return;
    }
    if (!days.some((d) => d.routine)) {
      setError('Add at least one day with a routine.');
      return;
    }

    setIsSaving(true);
    try {
      const saved = await createProgram({
        name: name.trim(),
        description: description.trim() || null,
        days: days.map((d) => ({ routineId: d.routine?.id ?? null })),
      });
      toast.success('Program saved.');
      router.replace(`/plans/programs/${saved.id}`);
      router.refresh();
    } catch (err) {
      setIsSaving(false);
      if (err instanceof ApiError && err.fieldErrors?.name) {
        setNameError(err.fieldErrors.name);
        return;
      }
      if (err instanceof ApiError && err.code === 'USER_ROUTINE_NOT_FOUND') {
        setError(
          'A routine is no longer available. Reload the page and check your program.',
        );
        return;
      }
      if (err instanceof ApiError && err.code === 'PROGRAM_LIMIT_REACHED') {
        setError('You already have the maximum number of programs.');
        return;
      }
      setError(fallbackErrorMessage(err));
    }
  }

  return (
    <div className="mx-auto flex w-full max-w-app flex-1 flex-col pb-8 lg:max-w-[560px]">
      <form onSubmit={handleSubmit} noValidate>
        <header className="sticky top-0 z-10 grid h-14 grid-cols-[1fr_minmax(0,2fr)_1fr] items-center border-b border-border-subtle bg-bg px-4">
          {/* A new program is only reached from the list, so going back avoids a second list entry. */}
          <button type="button" onClick={() => router.back()} className={cancelClass}>
            Cancel
          </button>
          <h1 className="truncate text-center text-[1.0625rem] font-semibold">
            New Program
          </h1>
          <Button
            type="submit"
            variant="primary"
            size="sm"
            className="justify-self-end"
            disabled={isSaving}
          >
            {isSaving ? 'Saving...' : 'Save'}
          </Button>
        </header>

        <div className="px-4 pt-4">
          <Label className="mb-1.5" htmlFor="program-name">
            Name
          </Label>
          <Input
            id="program-name"
            value={name}
            onChange={(e) => setName(e.target.value)}
            maxLength={128}
            autoComplete="off"
            aria-invalid={nameError ? true : undefined}
            aria-describedby={nameError ? 'program-name-error' : undefined}
          />
          {nameError && (
            <p
              id="program-name-error"
              className="mt-1.5 flex gap-1.5 text-meta text-danger"
            >
              <CircleAlert
                className="size-4 shrink-0 translate-y-0.5"
                aria-hidden="true"
              />
              {nameError}
            </p>
          )}

          <Label className="mt-4 mb-1.5" htmlFor="program-description">
            Description (optional)
          </Label>
          <textarea
            id="program-description"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            maxLength={1000}
            rows={2}
            className="w-full min-w-0 rounded-field border border-border-strong bg-surface-raised px-3 py-2.5 text-field text-text-primary placeholder:text-text-tertiary"
          />

          <h2 className="mt-6 text-title">Days</h2>
          <p className="mt-0.5 mb-3 text-meta text-text-secondary">
            In the order you train them. After the last day the program starts over.
          </p>
          {error && (
            <div
              role="alert"
              className="mb-3 flex items-start gap-2.5 rounded-field bg-danger-subtle p-3 text-meta"
            >
              <CircleAlert className="size-5 shrink-0 text-danger" aria-hidden="true" />
              <span>{error}</span>
            </div>
          )}

          {days.length > 0 && (
            <DndContext
              id="program-days"
              sensors={sensors}
              collisionDetection={closestCenter}
              onDragEnd={handleDragEnd}
            >
              <SortableContext
                items={days.map((d) => d.key)}
                strategy={verticalListSortingStrategy}
              >
                <ol className={listClass}>
                  {days.map((day, index) => (
                    <DayRow
                      key={day.key}
                      day={day}
                      number={index + 1}
                      onOpen={() => setSheet({ dayKey: day.key })}
                      onRemove={() => setDays(days.filter((d) => d.key !== day.key))}
                    />
                  ))}
                </ol>
              </SortableContext>
            </DndContext>
          )}

          {days.length < MAX_DAYS && (
            <Button
              type="button"
              variant="text"
              className="mt-2 -ml-2 px-2"
              onClick={() => setSheet({ dayKey: null })}
            >
              <Plus aria-hidden="true" />
              Add Day
            </Button>
          )}
        </div>
      </form>

      {sheet && (
        <DaySheet
          key={sheet.dayKey ?? 'new'}
          day={sheetDay}
          dayNumber={sheetDay ? sheetIndex + 1 : days.length + 1}
          routines={routines}
          canMoveUp={sheetIndex > 0}
          canMoveDown={sheetIndex >= 0 && sheetIndex < days.length - 1}
          onSelect={handleSelect}
          onMoveUp={() => setDays(arrayMove(days, sheetIndex, sheetIndex - 1))}
          onMoveDown={() => setDays(arrayMove(days, sheetIndex, sheetIndex + 1))}
          onRemove={() => {
            setDays(days.filter((d) => d.key !== sheet.dayKey));
            setSheet(null);
          }}
          onClose={() => setSheet(null)}
        />
      )}
    </div>
  );
}

type DayRowProps = {
  day: DayDraft;
  number: number;
  onOpen: () => void;
  onRemove: () => void;
};

function DayRow({ day, number, onOpen, onRemove }: DayRowProps) {
  const {
    attributes,
    listeners,
    setNodeRef,
    setActivatorNodeRef,
    transform,
    transition,
    isDragging,
  } = useSortable({ id: day.key });
  const label = day.routine?.name ?? 'Rest day';

  return (
    <li
      ref={setNodeRef}
      style={{ transform: CSS.Transform.toString(transform), transition }}
      className={cn('flex items-center', isDragging && 'relative z-10 bg-surface-raised')}
    >
      <button
        type="button"
        ref={setActivatorNodeRef}
        {...attributes}
        {...listeners}
        aria-label={`Reorder day ${number}, ${label}`}
        className="grid size-tap shrink-0 cursor-grab touch-none place-items-center text-text-tertiary hover:text-text-secondary"
      >
        <GripVertical className="size-5" aria-hidden="true" />
      </button>
      <button type="button" onClick={onOpen} className={cn(listRowClass, 'px-0')}>
        <span
          className="w-5 shrink-0 text-center font-semibold text-text-tertiary tabular-nums"
          aria-hidden="true"
        >
          {number}
        </span>
        <span className="min-w-0 flex-1">
          <span
            className={cn(
              'block truncate font-semibold',
              !day.routine && 'text-text-secondary',
            )}
          >
            {label}
          </span>
          {day.routine && (
            <span className="block text-meta text-text-tertiary tabular-nums">
              {day.routine.exerciseCount}{' '}
              {day.routine.exerciseCount === 1 ? 'exercise' : 'exercises'}
            </span>
          )}
        </span>
      </button>
      <button
        type="button"
        onClick={onRemove}
        aria-label={`Remove day ${number}, ${label}`}
        className="mr-1 grid size-tap shrink-0 place-items-center text-text-tertiary hover:text-danger"
      >
        <Trash2 className="size-5" aria-hidden="true" />
      </button>
    </li>
  );
}
