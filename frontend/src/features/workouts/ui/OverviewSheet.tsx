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
  sortableKeyboardCoordinates,
  useSortable,
  verticalListSortingStrategy,
} from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';
import { Check, GripVertical, Plus } from 'lucide-react';
import { useEffect, useRef } from 'react';

import { cn } from '@/shared/lib/utils';
import { Button } from '@/shared/ui/button';
import { listRowClass } from '@/shared/ui/list';
import { Sheet } from '@/shared/ui/sheet';
import type { SetRow } from '../lib/set-rows';
import type { WorkoutExercise } from '../types/api.types';

type OverviewSheetProps = {
  name: string;
  exercises: WorkoutExercise[];
  rows: Record<number, SetRow[]>;
  current: number;
  /** A new order is being saved, moving waits for it. */
  reordering: boolean;
  error: string | null;
  onSelect: (index: number) => void;
  onMove: (from: number, to: number) => void;
  onAdd: () => void;
  onClose: () => void;
};

//all exercises of the workout; the caller mounts it while open
export default function OverviewSheet({
  name,
  exercises,
  rows,
  current,
  reordering,
  error,
  onSelect,
  onMove,
  onAdd,
  onClose,
}: OverviewSheetProps) {
  const dialogRef = useRef<HTMLDialogElement>(null);
  const sensors = useSensors(
    useSensor(PointerSensor),
    useSensor(KeyboardSensor, { coordinateGetter: sortableKeyboardCoordinates }),
  );

  useEffect(() => {
    dialogRef.current?.showModal();
  }, []);

  function handleDragEnd({ active, over }: DragEndEvent) {
    if (!over || active.id === over.id) return;
    onMove(
      exercises.findIndex((e) => e.id === active.id),
      exercises.findIndex((e) => e.id === over.id),
    );
  }

  const count = exercises.length;
  const logged = Object.values(rows)
    .flat()
    .filter((row) => row.status === 'saved' || row.status === 'saving').length;

  return (
    <Sheet ref={dialogRef} onClose={onClose} aria-labelledby="overview-sheet-title">
      <div className="flex items-center justify-between gap-3">
        <h2 id="overview-sheet-title" className="truncate text-title">
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
      <p className="mt-1 mb-4 text-text-secondary tabular-nums">
        {count} {count === 1 ? 'exercise' : 'exercises'}, {logged}{' '}
        {logged === 1 ? 'set' : 'sets'} logged
      </p>

      {error && (
        <p role="alert" className="mb-3 text-meta text-danger">
          {error}
        </p>
      )}

      <DndContext
        id="workout-exercises"
        sensors={sensors}
        collisionDetection={closestCenter}
        onDragEnd={handleDragEnd}
      >
        <SortableContext
          items={exercises.map((e) => e.id)}
          strategy={verticalListSortingStrategy}
        >
          <ol className="divide-y divide-field-on-overlay">
            {exercises.map((entry, index) => (
              <EntryRow
                key={entry.id}
                entry={entry}
                number={index + 1}
                rows={rows[entry.id]}
                isCurrent={index === current}
                reordering={reordering}
                onSelect={() => onSelect(index)}
              />
            ))}
          </ol>
        </SortableContext>
      </DndContext>

      <Button type="button" className="mt-4 w-full" onClick={onAdd}>
        <Plus aria-hidden="true" />
        Add Exercise
      </Button>
    </Sheet>
  );
}

type EntryRowProps = {
  entry: WorkoutExercise;
  number: number;
  rows: SetRow[];
  isCurrent: boolean;
  reordering: boolean;
  onSelect: () => void;
};

function EntryRow({
  entry,
  number,
  rows,
  isCurrent,
  reordering,
  onSelect,
}: EntryRowProps) {
  const {
    attributes,
    listeners,
    setNodeRef,
    setActivatorNodeRef,
    transform,
    transition,
    isDragging,
  } = useSortable({ id: entry.id, disabled: reordering });

  const working = rows.filter((row) => row.setType === 'WORKING');
  const done = working.filter(
    (row) => row.status === 'saved' || row.status === 'saving',
  ).length;
  const complete = rows.length > 0 && rows.every((row) => row.status !== 'open');

  return (
    <li
      ref={setNodeRef}
      style={{ transform: CSS.Transform.toString(transform), transition }}
      className={cn(
        'flex items-center',
        isDragging && 'relative z-10 bg-field-on-overlay',
      )}
    >
      <button
        type="button"
        ref={setActivatorNodeRef}
        {...attributes}
        {...listeners}
        aria-label={`Reorder ${entry.exercise.name}`}
        className="-ml-3 grid size-tap shrink-0 cursor-grab touch-none place-items-center text-text-tertiary hover:text-text-secondary"
      >
        <GripVertical className="size-5" aria-hidden="true" />
      </button>
      <button
        type="button"
        onClick={onSelect}
        aria-current={isCurrent ? 'step' : undefined}
        className={cn(
          listRowClass,
          'min-w-0 flex-1 px-2 hover:bg-field-on-overlay active:bg-field-on-overlay',
          isCurrent && 'bg-field-on-overlay',
        )}
      >
        <span className="w-5 shrink-0 text-meta text-text-tertiary tabular-nums">
          {number}
        </span>
        <span className="min-w-0 flex-1">
          <span className="block truncate font-semibold">{entry.exercise.name}</span>
          <span className="block text-meta text-text-secondary tabular-nums">
            {done} of {working.length} {working.length === 1 ? 'set' : 'sets'}
          </span>
        </span>
        {complete && <Check className="size-5 shrink-0 text-success" aria-label="Done" />}
      </button>
    </li>
  );
}
