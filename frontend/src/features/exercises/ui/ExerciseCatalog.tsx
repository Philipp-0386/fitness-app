'use client';

import Link from 'next/link';
import { ChevronRight, Search } from 'lucide-react';
import { usePathname } from 'next/navigation';
import { useState } from 'react';

import { cn } from '@/shared/lib/utils';
import { Button } from '@/shared/ui/button';
import { Input } from '@/shared/ui/input';
import { listClass, listRowClass } from '@/shared/ui/list';
import { MuscleFilter, matchesMuscleFilter } from '../lib/muscle-filter';
import { typeLabels } from '../lib/type-labels';
import { Exercise, ExerciseType, MuscleGroup } from '../types/api.types';
import ExerciseTypeFilter from './ExerciseTypeFilter';
import MuscleGroupFilter from './MuscleGroupFilter';

const ownerOptions = [
  { value: 'all', label: 'All' },
  { value: 'yours', label: 'Yours' },
] as const;

type ExerciseCatalogProps = {
  exercises: Exercise[];
  muscleGroups: MuscleGroup[];
};

//filters the whole catalog in the browser, it is loaded once with the page
export default function ExerciseCatalog({
  exercises,
  muscleGroups,
}: ExerciseCatalogProps) {
  const pathname = usePathname();
  const [query, setQuery] = useState('');
  const [owner, setOwner] = useState<'all' | 'yours'>('all');
  const [type, setType] = useState<'all' | ExerciseType>('all');
  const [muscle, setMuscle] = useState<MuscleFilter | null>(null);

  const search = query.trim().toLowerCase();
  const visible = exercises.filter(
    (exercise) =>
      (owner === 'all' || exercise.custom) &&
      (type === 'all' || exercise.exerciseType === type) &&
      matchesMuscleFilter(exercise, muscle) &&
      (!search || exercise.name.toLowerCase().includes(search)),
  );

  return (
    <div className="px-4">
      <div className="relative mt-4">
        <label htmlFor="exercise-search" className="sr-only">
          Search exercises
        </label>
        <Search
          className="pointer-events-none absolute top-1/2 left-3 size-5 -translate-y-1/2 text-text-tertiary"
          aria-hidden="true"
        />
        <Input
          id="exercise-search"
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
          className="inline-grid grid-flow-col gap-1 rounded-card bg-surface p-1"
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
                  ? 'bg-surface-raised text-text-primary'
                  : 'text-text-tertiary hover:text-text-secondary',
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

      <p
        className="mt-3 mb-2 text-meta text-text-tertiary tabular-nums"
        aria-live="polite"
      >
        {visible.length} {visible.length === 1 ? 'exercise' : 'exercises'}
      </p>

      {visible.length > 0 ? (
        <ul className={listClass}>
          {visible.map((exercise) => {
            const primaryMuscles = exercise.muscles
              .filter((muscle) => muscle.role === 'PRIMARY')
              .map((muscle) => muscle.name);
            const href = `/exercises/${exercise.id}`;
            const isActive = pathname === href;
            return (
              <li key={exercise.id}>
                <Link
                  href={href}
                  aria-current={isActive ? 'page' : undefined}
                  className={cn(listRowClass, isActive && 'lg:bg-surface-raised')}
                >
                  <span className="min-w-0 flex-1">
                    <span className="block font-semibold">{exercise.name}</span>
                    {primaryMuscles.length > 0 && (
                      <span className="block text-meta text-text-tertiary">
                        {primaryMuscles.length === 1 ? 'Muscle' : 'Muscles'}:{' '}
                        <span className="text-text-secondary">
                          {primaryMuscles.join(', ')}
                        </span>
                      </span>
                    )}
                  </span>
                  {exercise.custom && (
                    <span className="shrink-0 rounded-full border border-border-strong px-2 text-caption font-semibold text-text-secondary">
                      Yours
                    </span>
                  )}
                  <ChevronRight
                    className="size-5 shrink-0 text-text-tertiary"
                    aria-hidden="true"
                  />
                </Link>
              </li>
            );
          })}
        </ul>
      ) : (
        <div className="px-6 py-12 text-center">
          <h2 className="text-title">No matching exercises</h2>
          <p className="mt-1.5 text-text-secondary">
            Nothing matches
            {search && ` "${query.trim()}"`}
            {owner === 'yours' && ' among your exercises'}
            {type !== 'all' && ` in ${typeLabels[type]}`}
            {muscle && ` targeting ${muscle.label}`}.
          </p>
          <Button
            type="button"
            className="mt-5"
            onClick={() => {
              setQuery('');
              setOwner('all');
              setType('all');
              setMuscle(null);
            }}
          >
            Clear search and filters
          </Button>
        </div>
      )}
    </div>
  );
}
