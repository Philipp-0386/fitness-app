'use client';

import { Search } from 'lucide-react';
import { useEffect, useRef, useState } from 'react';

import { cn } from '@/shared/lib/utils';
import { Button } from '@/shared/ui/button';
import { Input } from '@/shared/ui/input';
import { listRowClass } from '@/shared/ui/list';
import { Sheet } from '@/shared/ui/sheet';
import { MuscleFilter, matchesMuscleFilter } from '../lib/muscle-filter';
import { typeLabels } from '../lib/type-labels';
import { Exercise, ExerciseType, MuscleGroup } from '../types/api.types';
import ExerciseTypeFilter from './ExerciseTypeFilter';
import MuscleGroupFilter from './MuscleGroupFilter';

const ownerOptions = [
  { value: 'all', label: 'All' },
  { value: 'yours', label: 'Yours' },
] as const;

type ExercisePickerProps = {
  exercises: Exercise[];
  muscleGroups: MuscleGroup[];
  open: boolean;
  onSelect: (exercise: Exercise) => void;
  onClose: () => void;
};

//sheet for choosing one exercise, opened and closed by the caller
export default function ExercisePicker({
  exercises,
  muscleGroups,
  open,
  onSelect,
  onClose,
}: ExercisePickerProps) {
  const dialogRef = useRef<HTMLDialogElement>(null);
  const [query, setQuery] = useState('');
  const [owner, setOwner] = useState<'all' | 'yours'>('all');
  const [type, setType] = useState<'all' | ExerciseType>('all');
  const [muscle, setMuscle] = useState<MuscleFilter | null>(null);

  useEffect(() => {
    const dialog = dialogRef.current;
    if (!dialog) return;

    if (open && !dialog.open) dialog.showModal();
    if (!open && dialog.open) dialog.close();
  }, [open]);

  // Runs for every way of closing: cancel, Escape and a selection the caller closes the sheet for.
  function handleClose() {
    setQuery('');
    setOwner('all');
    setType('all');
    setMuscle(null);
    onClose();
  }

  const search = query.trim().toLowerCase();
  const visible = exercises.filter(
    (exercise) =>
      (owner === 'all' || exercise.custom) &&
      (type === 'all' || exercise.exerciseType === type) &&
      matchesMuscleFilter(exercise, muscle) &&
      (!search || exercise.name.toLowerCase().includes(search)),
  );

  return (
    <Sheet ref={dialogRef} onClose={handleClose} aria-labelledby="exercise-picker-title">
      <div className="flex items-center justify-between gap-3">
        <h2 id="exercise-picker-title" className="text-title">
          Choose an exercise
        </h2>
        <Button type="button" variant="text" size="sm" onClick={onClose}>
          Cancel
        </Button>
      </div>

      <div className="relative mt-3">
        <label htmlFor="exercise-picker-search" className="sr-only">
          Search exercises
        </label>
        <Search
          className="pointer-events-none absolute top-1/2 left-3 size-5 -translate-y-1/2 text-text-secondary"
          aria-hidden="true"
        />
        <Input
          id="exercise-picker-search"
          type="search"
          placeholder="Search exercises"
          autoComplete="off"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          className="pl-10"
        />
      </div>

      <div className="mt-3 flex items-center gap-2.5">
        <div
          role="radiogroup"
          aria-label="Show"
          className="inline-grid grid-flow-col gap-1 rounded-card bg-field-on-overlay p-1"
        >
          {ownerOptions.map(({ value, label }) => (
            <button
              key={value}
              type="button"
              role="radio"
              aria-checked={owner === value}
              onClick={() => setOwner(value)}
              className={cn(
                'min-h-9 rounded-field px-3.5 text-meta font-semibold',
                owner === value
                  ? 'bg-surface-overlay text-text-primary'
                  : 'text-text-secondary hover:text-text-primary',
              )}
            >
              {label}
            </button>
          ))}
        </div>
        <ExerciseTypeFilter value={type} onChange={setType} />
        <MuscleGroupFilter
          muscleGroups={muscleGroups}
          value={muscle}
          onChange={setMuscle}
        />
      </div>

      {visible.length > 0 ? (
        <ul className="mt-3 divide-y divide-field-on-overlay">
          {visible.map((exercise) => {
            const primaryMuscles = exercise.muscles
              .filter((m) => m.role === 'PRIMARY')
              .map((m) => m.name);
            return (
              <li key={exercise.id}>
                <button
                  type="button"
                  onClick={() => onSelect(exercise)}
                  className={cn(
                    listRowClass,
                    'px-1 hover:bg-field-on-overlay active:bg-field-on-overlay',
                  )}
                >
                  <span className="min-w-0 flex-1">
                    <span className="block font-semibold">{exercise.name}</span>
                    {primaryMuscles.length > 0 && (
                      <span className="block text-meta text-text-secondary">
                        {primaryMuscles.length === 1 ? 'Muscle' : 'Muscles'}:{' '}
                        {primaryMuscles.join(', ')}
                      </span>
                    )}
                  </span>
                  {exercise.custom && (
                    <span className="shrink-0 rounded-full border border-border-on-overlay px-2 text-caption font-semibold text-text-secondary">
                      Yours
                    </span>
                  )}
                </button>
              </li>
            );
          })}
        </ul>
      ) : (
        <p className="mt-6 mb-4 text-center text-text-secondary">
          No matching exercises
          {search && ` for "${query.trim()}"`}
          {owner === 'yours' && ' among your exercises'}
          {type !== 'all' && ` in ${typeLabels[type]}`}
          {muscle && ` targeting ${muscle.label}`}.
        </p>
      )}
    </Sheet>
  );
}
