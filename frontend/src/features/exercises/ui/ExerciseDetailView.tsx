import Link from 'next/link';

import { TopBar } from '@/shared/layout';
import { cn } from '@/shared/lib/utils';
import { Button } from '@/shared/ui/button';
import { listClass } from '@/shared/ui/list';
import { trackingOptions } from '../lib/tracking-options';
import { typeLabels } from '../lib/type-labels';
import { Exercise } from '../types/api.types';

type ExerciseDetailViewProps = {
  exercise: Exercise;
};

//own exercises get an Edit button, global ones are read only
export default function ExerciseDetailView({ exercise }: ExerciseDetailViewProps) {
  const tracking = trackingOptions.find((o) => o.value === exercise.trackingType);

  return (
    <div className="mx-auto flex w-full max-w-app flex-1 flex-col pb-8 lg:max-w-[560px]">
      <TopBar
        backHref="/exercises"
        backLabel="Exercises"
        title={exercise.name}
        inSplitView
        action={
          exercise.custom && (
            <Button asChild size="sm">
              <Link href={`/exercises/${exercise.id}/edit`} replace>
                Edit
              </Link>
            </Button>
          )
        }
      />

      {exercise.custom && (
        <p className="mt-1 px-4">
          <span className="rounded-full border border-border-strong px-2 text-caption font-semibold text-text-secondary">
            Yours
          </span>
        </p>
      )}

      {exercise.description && (
        <p className="mt-3 max-w-[62ch] px-4 text-text-secondary">
          {exercise.description}
        </p>
      )}

      <dl className={cn(listClass, 'mx-4 mt-5')}>
        <div className="flex min-h-14 items-center gap-3 px-4 py-2.5">
          <dt className="min-w-0 flex-1 text-text-secondary">Type</dt>
          <dd className="shrink-0 font-semibold">{typeLabels[exercise.exerciseType]}</dd>
        </div>
        {tracking && (
          <div className="flex min-h-14 items-center gap-3 px-4 py-2.5">
            <dt className="min-w-0 flex-1 text-text-secondary">Logged per set</dt>
            <dd className="shrink-0 font-semibold">{tracking.label}</dd>
          </div>
        )}
      </dl>

      <section className="mt-6 px-4" aria-labelledby="muscles-title">
        <h2 id="muscles-title" className="text-title">
          Muscles
        </h2>
        {exercise.muscles.length > 0 ? (
          <ul className={cn(listClass, 'mt-3')}>
            {exercise.muscles.map((muscle) => (
              <li
                key={muscle.id}
                className="flex min-h-14 items-center gap-3 px-4 py-2.5"
              >
                <span className="min-w-0 flex-1 font-semibold">{muscle.name}</span>
                <span className="shrink-0 text-meta text-text-secondary">
                  {muscle.role === 'PRIMARY' ? 'Primary' : 'Secondary'}
                </span>
              </li>
            ))}
          </ul>
        ) : (
          <p className="mt-1.5 text-text-secondary">No muscles assigned yet.</p>
        )}
      </section>

      <section className="mt-6 px-4" aria-labelledby="instructions-title">
        <h2 id="instructions-title" className="text-title">
          Instructions
        </h2>
        <p className="mt-1.5 max-w-[62ch] whitespace-pre-line text-text-secondary">
          {exercise.instructions || 'No instructions yet.'}
        </p>
      </section>
    </div>
  );
}
