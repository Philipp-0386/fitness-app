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
import { CircleAlert, GripVertical, Plus } from 'lucide-react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useRef, useState } from 'react';
import { toast } from 'sonner';

import ExercisePicker from '@/features/exercises/ui/ExercisePicker';
import { trackingFields } from '@/features/exercises/lib/tracking-fields';
import type { Exercise } from '@/features/exercises';
import { ApiError } from '@/shared/api/errors/api-error';
import { fallbackErrorMessage } from '@/shared/api/errors/fallback-error-message';
import { cn } from '@/shared/lib/utils';
import { Button } from '@/shared/ui/button';
import { Input } from '@/shared/ui/input';
import { Label } from '@/shared/ui/label';
import { listClass, listRowClass } from '@/shared/ui/list';
import { createRoutine } from '../client/create-routine.client';
import { updateRoutine } from '../client/update-routine.client';
import { formatTarget } from '../lib/format-target';
import { RoutineDetail } from '../types/api.types';
import { Slot } from '../types/editor.types';
import DeleteRoutineDialog from './DeleteRoutineDialog';
import TargetSheet from './TargetSheet';

const MAX_SLOTS = 50;

const emptySlot: Omit<Slot, 'key'> = {
  exercise: null,
  targetSets: null,
  targetRepsMin: null,
  targetRepsMax: null,
  targetDurationSeconds: null,
  targetDistanceMeters: null,
};

type OpenSheet = { kind: 'picker' | 'targets'; slotKey: string } | null;

type RoutineEditorProps = {
  /** null for a new routine. */
  routine: RoutineDetail | null;
  exercises: Exercise[];
};

export default function RoutineEditor({ routine, exercises }: RoutineEditorProps) {
  const router = useRouter();
  const addedSlots = useRef(0);
  const [name, setName] = useState(routine?.name ?? '');
  const [description, setDescription] = useState(routine?.description ?? '');
  const [slots, setSlots] = useState<Slot[]>(() =>
    routine
      ? routine.exercises.map((re) => ({
          key: `saved-${re.id}`,
          exercise: re.exercise,
          targetSets: re.targetSets,
          targetRepsMin: re.targetRepsMin,
          targetRepsMax: re.targetRepsMax,
          targetDurationSeconds: re.targetDurationSeconds,
          targetDistanceMeters: re.targetDistanceMeters,
        }))
      : Array.from({ length: 5 }, (_, i) => ({ key: `initial-${i}`, ...emptySlot })),
  );
  const [sheet, setSheet] = useState<OpenSheet>(null);
  const [nameError, setNameError] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [isSaving, setIsSaving] = useState(false);

  const sensors = useSensors(
    useSensor(PointerSensor),
    useSensor(KeyboardSensor, { coordinateGetter: sortableKeyboardCoordinates }),
  );

  const sheetIndex = sheet ? slots.findIndex((s) => s.key === sheet.slotKey) : -1;
  const sheetSlot = sheetIndex >= 0 ? slots[sheetIndex] : null;
  const cancelHref = routine ? `/routines/${routine.id}` : '/routines';

  function handleSelect(exercise: Exercise) {
    if (!sheet) return;
    const fields = trackingFields(exercise.trackingType);
    setSlots(
      slots.map((s) =>
        s.key === sheet.slotKey
          ? {
              ...s,
              exercise: {
                id: exercise.id,
                name: exercise.name,
                trackingType: exercise.trackingType,
                deleted: false,
              },
              targetRepsMin: fields.reps ? s.targetRepsMin : null,
              targetRepsMax: fields.reps ? s.targetRepsMax : null,
              targetDurationSeconds: fields.duration ? s.targetDurationSeconds : null,
              targetDistanceMeters: fields.distance ? s.targetDistanceMeters : null,
            }
          : s,
      ),
    );
    setSheet({ kind: 'targets', slotKey: sheet.slotKey });
  }

  // The close event of a sheet arrives after the state may already have switched to the other sheet.
  function closeSheet(kind: 'picker' | 'targets') {
    setSheet((current) => (current?.kind === kind ? null : current));
  }

  function handleDragEnd({ active, over }: DragEndEvent) {
    if (!over || active.id === over.id) return;
    const from = slots.findIndex((s) => s.key === active.id);
    const to = slots.findIndex((s) => s.key === over.id);
    setSlots(arrayMove(slots, from, to));
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
    if (slots.some((s) => s.exercise?.deleted)) {
      setError('Replace or remove the exercises that are no longer in the catalog.');
      return;
    }

    const payload = {
      name: name.trim(),
      description: description.trim() || null,
      exercises: slots.flatMap((s) =>
        s.exercise
          ? [
              {
                exerciseId: s.exercise.id,
                targetSets: s.targetSets,
                targetRepsMin: s.targetRepsMin,
                targetRepsMax: s.targetRepsMax,
                targetDurationSeconds: s.targetDurationSeconds,
                targetDistanceMeters: s.targetDistanceMeters,
              },
            ]
          : [],
      ),
    };

    setIsSaving(true);
    try {
      const saved = routine
        ? await updateRoutine(String(routine.id), payload)
        : await createRoutine(payload);
      toast.success('Routine saved.');
      router.push(`/routines/${saved.id}`);
      router.refresh();
    } catch (err) {
      setIsSaving(false);
      if (err instanceof ApiError && err.fieldErrors?.name) {
        setNameError(err.fieldErrors.name);
        return;
      }
      if (err instanceof ApiError && err.code === 'EXERCISE_NOT_FOUND') {
        setError(
          'An exercise is no longer available. Reload the page and check your routine.',
        );
        return;
      }
      setError(fallbackErrorMessage(err));
    }
  }

  return (
    <div className="mx-auto flex w-full max-w-app flex-1 flex-col pb-8 lg:max-w-[560px]">
      <form onSubmit={handleSubmit} noValidate>
        <header className="sticky top-0 z-10 grid h-14 grid-cols-[1fr_minmax(0,2fr)_1fr] items-center border-b border-border-subtle bg-bg px-4">
          <Link
            href={cancelHref}
            className="-ml-2 flex min-h-tap items-center justify-self-start px-2 font-semibold text-text-secondary hover:text-text-primary"
          >
            Cancel
          </Link>
          <h1 className="truncate text-center text-[1.0625rem] font-semibold">
            {routine ? routine.name : 'New routine'}
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
          <Label className="mb-1.5" htmlFor="routine-name">
            Name
          </Label>
          <Input
            id="routine-name"
            value={name}
            onChange={(e) => setName(e.target.value)}
            maxLength={128}
            autoComplete="off"
            aria-invalid={nameError ? true : undefined}
            aria-describedby={nameError ? 'routine-name-error' : undefined}
          />
          {nameError && (
            <p
              id="routine-name-error"
              className="mt-1.5 flex gap-1.5 text-meta text-danger"
            >
              <CircleAlert
                className="size-4 shrink-0 translate-y-0.5"
                aria-hidden="true"
              />
              {nameError}
            </p>
          )}

          <Label className="mt-4 mb-1.5" htmlFor="routine-description">
            Description (optional)
          </Label>
          <textarea
            id="routine-description"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            maxLength={1000}
            rows={2}
            className="w-full min-w-0 rounded-field border border-border-strong bg-surface-raised px-3 py-2.5 text-field text-text-primary placeholder:text-text-tertiary"
          />

          <h2 className="mt-6 mb-3 text-title">Exercises</h2>
          {error && (
            <div
              role="alert"
              className="mb-3 flex items-start gap-2.5 rounded-field bg-danger-subtle p-3 text-meta"
            >
              <CircleAlert className="size-5 shrink-0 text-danger" aria-hidden="true" />
              <span>{error}</span>
            </div>
          )}

          {slots.length > 0 && (
            <DndContext
              id="routine-slots"
              sensors={sensors}
              collisionDetection={closestCenter}
              onDragEnd={handleDragEnd}
            >
              <SortableContext
                items={slots.map((s) => s.key)}
                strategy={verticalListSortingStrategy}
              >
                <ul className={listClass}>
                  {slots.map((slot) => (
                    <SlotRow
                      key={slot.key}
                      slot={slot}
                      onOpen={() =>
                        setSheet({
                          kind: slot.exercise ? 'targets' : 'picker',
                          slotKey: slot.key,
                        })
                      }
                    />
                  ))}
                </ul>
              </SortableContext>
            </DndContext>
          )}

          {slots.length < MAX_SLOTS && (
            <Button
              type="button"
              variant="text"
              className="mt-2 -ml-2 px-2"
              onClick={() =>
                setSlots([
                  ...slots,
                  { key: `added-${++addedSlots.current}`, ...emptySlot },
                ])
              }
            >
              <Plus aria-hidden="true" />
              Add slot
            </Button>
          )}
        </div>
      </form>

      {routine && (
        <div className="mx-4 mt-8 border-t border-border-subtle pt-2">
          <DeleteRoutineDialog routineId={routine.id} />
        </div>
      )}

      <ExercisePicker
        exercises={exercises}
        open={sheet?.kind === 'picker'}
        onSelect={handleSelect}
        onClose={() => closeSheet('picker')}
      />

      {sheet?.kind === 'targets' && sheetSlot?.exercise && (
        <TargetSheet
          key={sheetSlot.key}
          slot={sheetSlot}
          exercise={sheetSlot.exercise}
          canMoveUp={sheetIndex > 0}
          canMoveDown={sheetIndex < slots.length - 1}
          onDone={(targets) => {
            setSlots(
              slots.map((s) => (s.key === sheetSlot.key ? { ...s, ...targets } : s)),
            );
            setSheet(null);
          }}
          onChangeExercise={() => setSheet({ kind: 'picker', slotKey: sheetSlot.key })}
          onMoveUp={() => setSlots(arrayMove(slots, sheetIndex, sheetIndex - 1))}
          onMoveDown={() => setSlots(arrayMove(slots, sheetIndex, sheetIndex + 1))}
          onRemove={() => {
            setSlots(slots.filter((s) => s.key !== sheetSlot.key));
            setSheet(null);
          }}
          onClose={() => closeSheet('targets')}
        />
      )}
    </div>
  );
}

type SlotRowProps = {
  slot: Slot;
  onOpen: () => void;
};

function SlotRow({ slot, onOpen }: SlotRowProps) {
  const {
    attributes,
    listeners,
    setNodeRef,
    setActivatorNodeRef,
    transform,
    transition,
    isDragging,
  } = useSortable({ id: slot.key, disabled: slot.exercise === null });

  return (
    <li
      ref={setNodeRef}
      style={{ transform: CSS.Transform.toString(transform), transition }}
      className={cn('flex items-center', isDragging && 'relative z-10 bg-surface-raised')}
    >
      {slot.exercise ? (
        <button
          type="button"
          ref={setActivatorNodeRef}
          {...attributes}
          {...listeners}
          aria-label={`Reorder ${slot.exercise.name}`}
          className="grid size-tap shrink-0 cursor-grab touch-none place-items-center text-text-tertiary hover:text-text-secondary"
        >
          <GripVertical className="size-5" aria-hidden="true" />
        </button>
      ) : (
        <span className="size-tap shrink-0" aria-hidden="true" />
      )}
      <button type="button" onClick={onOpen} className={cn(listRowClass, 'pl-0')}>
        {slot.exercise ? (
          <>
            <span className="min-w-0 flex-1">
              <span className="block truncate font-semibold">{slot.exercise.name}</span>
              {slot.exercise.deleted && (
                <span className="block text-meta text-warning">
                  No longer in the catalog, replace or remove it
                </span>
              )}
            </span>
            {!slot.exercise.deleted && (
              <span className="shrink-0 text-meta text-text-secondary tabular-nums">
                {formatTarget(slot)}
              </span>
            )}
          </>
        ) : (
          <span className="flex-1 rounded-field border border-dashed border-border-strong px-3 py-2 text-text-tertiary">
            Tap to choose an exercise
          </span>
        )}
      </button>
    </li>
  );
}
