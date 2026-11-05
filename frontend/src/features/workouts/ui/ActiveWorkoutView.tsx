'use client';

import { ChevronLeft, ChevronRight } from 'lucide-react';
import { useState } from 'react';

import { formatTarget } from '@/features/routines/lib/format-target';
import { Button } from '@/shared/ui/button';
import type { Workout } from '../types/api.types';
import WorkoutTopBar from './WorkoutTopBar';

type ActiveWorkoutViewProps = {
  workout: Workout;
};

export default function ActiveWorkoutView({ workout }: ActiveWorkoutViewProps) {
  const [current, setCurrent] = useState(0);
  const entry = workout.exercises[current];
  const last = workout.exercises.length - 1;

  return (
    <div className="mx-auto flex w-full max-w-app flex-1 flex-col">
      <WorkoutTopBar
        name={workout.name}
        startedAt={workout.startedAt}
        current={current}
        total={workout.exercises.length}
      />

      {workout.exercises.length === 0 ? (
        <main className="flex-1 px-4 pt-2">
          <p className="text-text-secondary">No exercises yet.</p>
        </main>
      ) : (
        <>
          <main className="flex-1 px-4 pt-2">
            <h1 className="text-display text-balance">{entry.exercise.name}</h1>
            <p className="mt-1 text-text-secondary tabular-nums">{formatTarget(entry)}</p>
            <div className="mt-6 rounded-card bg-surface px-4 py-8 text-center text-text-tertiary">
              tracking here
            </div>
          </main>

          <div className="sticky bottom-0 grid grid-cols-2 gap-3 bg-bg px-4 pt-3 pb-[calc(1rem+env(safe-area-inset-bottom))]">
            <Button
              type="button"
              onClick={() => setCurrent(current - 1)}
              disabled={current === 0}
            >
              <ChevronLeft aria-hidden="true" />
              Previous
            </Button>
            <Button
              type="button"
              onClick={() => setCurrent(current + 1)}
              disabled={current === last}
            >
              Next
              <ChevronRight aria-hidden="true" />
            </Button>
          </div>
        </>
      )}
    </div>
  );
}
