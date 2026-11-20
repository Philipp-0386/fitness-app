import { CircleCheck } from 'lucide-react';
import Link from 'next/link';

import { Button } from '@/shared/ui/button';
import { formatDuration } from '../lib/format-duration';
import type { Workout } from '../types/api.types';

//shown right after finishing: which routine, how long, how many sets
export default function WorkoutSummaryView({ workout }: { workout: Workout }) {
  const setCount = workout.exercises.reduce((sum, entry) => sum + entry.sets.length, 0);

  return (
    <div className="mx-auto flex w-full max-w-app flex-1 flex-col">
      <main className="flex flex-1 flex-col items-center justify-center px-4 text-center">
        <CircleCheck className="size-12 text-success" aria-hidden="true" />
        <h1 className="mt-4 text-headline">Workout Complete</h1>
        <p className="mt-1 text-text-secondary">{workout.name}</p>

        <dl className="mt-8 grid w-full grid-cols-2 gap-2">
          {[
            {
              label: 'Duration',
              value: formatDuration(
                workout.startedAt,
                workout.endedAt ?? workout.startedAt,
              ),
            },
            { label: 'Sets', value: String(setCount) },
          ].map((stat) => (
            <div key={stat.label}>
              <dt className="text-meta text-text-secondary">{stat.label}</dt>
              <dd className="text-title tabular-nums">{stat.value}</dd>
            </div>
          ))}
        </dl>
      </main>

      <div className="sticky bottom-0 bg-bg px-4 pt-3 pb-[calc(1rem+env(safe-area-inset-bottom))]">
        <Button asChild variant="primary" size="lg">
          <Link href="/dashboard">Done</Link>
        </Button>
      </div>
    </div>
  );
}
