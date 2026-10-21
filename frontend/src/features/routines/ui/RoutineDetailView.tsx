import { TopBar } from '@/shared/layout';
import { cn } from '@/shared/lib/utils';
import { listClass } from '@/shared/ui/list';
import { RoutineDetail, RoutineExercise } from '../types/api.types';

const trackingHints: Partial<
  Record<RoutineExercise['exercise']['trackingType'], string>
> = {
  BODYWEIGHT_REPS: '+kg is added weight',
  ASSISTED_REPS: '-kg is the assistance',
};

/** Formats the targets as "3 x 8-12", "3 x 60 s" or "5 km, 30 min"; empty when none are set. */
function formatTarget(re: RoutineExercise): string {
  const values: string[] = [];

  if (re.targetRepsMin !== null || re.targetRepsMax !== null) {
    const min = re.targetRepsMin ?? re.targetRepsMax;
    const max = re.targetRepsMax ?? re.targetRepsMin;
    values.push(min === max ? `${min}` : `${min}-${max}`);
  }
  if (re.targetDistanceMeters !== null) {
    const m = re.targetDistanceMeters;
    values.push(m >= 1000 ? `${m / 1000} km` : `${m} m`);
  }
  if (re.targetDurationSeconds !== null) {
    const s = re.targetDurationSeconds;
    values.push(s % 60 === 0 ? `${s / 60} min` : `${s} s`);
  }

  const value = values.join(', ');
  if (re.targetSets === null) return value;
  if (!value) return `${re.targetSets} ${re.targetSets === 1 ? 'set' : 'sets'}`;
  return `${re.targetSets} x ${value}`;
}

type RoutineDetailViewProps = {
  routine: RoutineDetail;
};

export default function RoutineDetailView({ routine }: RoutineDetailViewProps) {
  return (
    <div className="mx-auto flex w-full max-w-app flex-1 flex-col pb-8 lg:max-w-[560px]">
      <TopBar backHref="/routines" backLabel="Plans" title={routine.name} inSplitView />

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
