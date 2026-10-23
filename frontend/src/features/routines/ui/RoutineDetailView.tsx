import Link from 'next/link';

import { TopBar } from '@/shared/layout';
import { cn } from '@/shared/lib/utils';
import { Button } from '@/shared/ui/button';
import { listClass } from '@/shared/ui/list';
import { formatTarget } from '../lib/format-target';
import { RoutineDetail, RoutineExercise } from '../types/api.types';

const trackingHints: Partial<
  Record<RoutineExercise['exercise']['trackingType'], string>
> = {
  BODYWEIGHT_REPS: '+kg is added weight',
  ASSISTED_REPS: '-kg is the assistance',
};

type RoutineDetailViewProps = {
  routine: RoutineDetail;
  /** Own routines get an Edit button, presets a Copy button. */
  kind: 'own' | 'preset';
};

export default function RoutineDetailView({ routine, kind }: RoutineDetailViewProps) {
  return (
    <div className="mx-auto flex w-full max-w-app flex-1 flex-col pb-8 lg:max-w-[560px]">
      <TopBar
        backHref="/routines"
        backLabel="Plans"
        title={routine.name}
        inSplitView
        action={
          <Button asChild size="sm">
            {kind === 'own' ? (
              <Link href={`/routines/${routine.id}/edit`} replace>
                Edit
              </Link>
            ) : (
              <Link href={`/routines/presets/${routine.id}/copy`} replace>
                Copy
              </Link>
            )}
          </Button>
        }
      />

      {routine.description && (
        <p className="mt-1 px-4 text-text-secondary">{routine.description}</p>
      )}

      {routine.exercises.length === 0 ? (
        <p className="mt-5 px-4 text-text-secondary">No exercises in this routine yet.</p>
      ) : (
        <ul className={cn(listClass, 'mx-4 mt-5')}>
          {routine.exercises.map((re) => {
            const sub = re.exercise.deleted
              ? 'No longer in the catalog'
              : trackingHints[re.exercise.trackingType];
            return (
              <li key={re.id} className="flex min-h-14 items-center gap-3 px-4 py-2.5">
                <span className="min-w-0 flex-1">
                  <span className="block font-semibold">{re.exercise.name}</span>
                  {sub && (
                    <span className="block text-meta text-text-tertiary">{sub}</span>
                  )}
                </span>
                <span className="shrink-0 text-meta text-text-secondary tabular-nums">
                  {formatTarget(re)}
                </span>
              </li>
            );
          })}
        </ul>
      )}
    </div>
  );
}
