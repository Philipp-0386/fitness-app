import { formatTarget } from '@/features/routines/lib/format-target';
import { TopBar } from '@/shared/layout';
import { cn } from '@/shared/lib/utils';
import { listClass } from '@/shared/ui/list';
import { formatDuration } from '../lib/format-duration';
import { formatSetValues } from '../lib/format-set';
import { TIME_ZONE } from '../lib/group-by-week';
import type { Workout } from '../types/api.types';

const dateFormat = new Intl.DateTimeFormat('en-GB', {
  timeZone: TIME_ZONE,
  weekday: 'short',
  day: 'numeric',
  month: 'short',
  year: 'numeric',
});
const timeFormat = new Intl.DateTimeFormat('en-GB', {
  timeZone: TIME_ZONE,
  hour: '2-digit',
  minute: '2-digit',
});

//a past workout read only: when, how long, and every exercise with its logged sets
export default function WorkoutDetailView({ workout }: { workout: Workout }) {
  const started = new Date(workout.startedAt);
  const setCount = workout.exercises.reduce((sum, entry) => sum + entry.sets.length, 0);

  return (
    <div className="mx-auto flex w-full max-w-app flex-1 flex-col pb-8 lg:max-w-[560px]">
      <TopBar backHref="/history" backLabel="History" title={workout.name} inSplitView />

      <p className="mt-1 px-4 text-text-secondary tabular-nums">
        {dateFormat.format(started)}, {timeFormat.format(started)}
        {workout.endedAt ? `-${timeFormat.format(new Date(workout.endedAt))}` : ''}
      </p>

      <dl className="mx-4 mt-4 grid grid-cols-3 gap-2">
        {[
          {
            label: 'Duration',
            value: workout.endedAt
              ? formatDuration(workout.startedAt, workout.endedAt)
              : 'In progress',
          },
          { label: 'Sets', value: String(setCount) },
          { label: 'Exercises', value: String(workout.exercises.length) },
        ].map((stat) => (
          <div key={stat.label} className="rounded-card bg-surface px-3 py-2.5">
            <dt className="text-meta text-text-secondary">{stat.label}</dt>
            <dd className="font-semibold tabular-nums">{stat.value}</dd>
          </div>
        ))}
      </dl>

      {workout.exercises.length === 0 && (
        <p className="mt-6 px-4 text-text-secondary">No exercises in this workout.</p>
      )}

      {workout.exercises.map((entry) => {
        const type = entry.exercise.trackingType;
        const target = formatTarget(entry);
        return (
          <section
            key={entry.id}
            className="mx-4 mt-6"
            aria-labelledby={`entry-${entry.id}`}
          >
            <h2 id={`entry-${entry.id}`} className="text-title">
              {entry.exercise.name}
            </h2>
            {(target || entry.exercise.deleted) && (
              <p className="mt-0.5 text-meta text-text-tertiary tabular-nums">
                {[
                  target && `Target ${target}`,
                  entry.exercise.deleted && 'No longer in the catalog',
                ]
                  .filter(Boolean)
                  .join('. ')}
              </p>
            )}

            {entry.sets.length === 0 ? (
              <p className="mt-2 text-meta text-text-secondary">No sets logged.</p>
            ) : (
              <ol className={cn(listClass, 'mt-2')}>
                {entry.sets.map((set, index) => {
                  const workingNumber = entry.sets
                    .slice(0, index + 1)
                    .filter((s) => s.setType === 'WORKING').length;
                  return (
                    <li
                      key={set.clientId}
                      className="grid min-h-11 grid-cols-[2.5rem_minmax(0,1fr)_auto] items-center gap-2 px-4 tabular-nums"
                    >
                      <span
                        className="font-semibold"
                        aria-label={
                          set.setType === 'WORKING'
                            ? `Set ${workingNumber}`
                            : set.setType === 'WARMUP'
                              ? 'Warm-up'
                              : 'Drop set'
                        }
                      >
                        {set.setType === 'WORKING'
                          ? workingNumber
                          : set.setType === 'WARMUP'
                            ? 'W'
                            : 'D'}
                      </span>
                      <span>{formatSetValues(type, set)}</span>
                      <span className="text-meta text-text-secondary">
                        {set.rpe !== null && `RPE ${set.rpe}`}
                      </span>
                    </li>
                  );
                })}
              </ol>
            )}
          </section>
        );
      })}
    </div>
  );
}
