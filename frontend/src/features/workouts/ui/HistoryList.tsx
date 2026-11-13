'use client';

import { ChevronRight } from 'lucide-react';
import Link from 'next/link';
import { useState } from 'react';

import { fallbackErrorMessage } from '@/shared/api/errors/fallback-error-message';
import { Button } from '@/shared/ui/button';
import { listClass, listRowClass } from '@/shared/ui/list';
import { fetchWorkoutHistoryPage } from '../client/fetch-workout-history.client';
import { formatDuration } from '../lib/format-duration';
import { TIME_ZONE, groupByWeek } from '../lib/group-by-week';
import type { WorkoutHistory } from '../types/api.types';

const dayFormat = new Intl.DateTimeFormat('en-GB', {
  timeZone: TIME_ZONE,
  weekday: 'short',
  day: 'numeric',
  month: 'short',
});

//finished workouts grouped by week; the first page comes from the server, "Load More" appends the next ones
export default function HistoryList({ initial }: { initial: WorkoutHistory }) {
  const [workouts, setWorkouts] = useState(initial.workouts);
  const [hasNext, setHasNext] = useState(initial.hasNext);
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function loadMore() {
    setLoading(true);
    setError(null);
    try {
      const next = await fetchWorkoutHistoryPage(page + 1);
      //a workout finished meanwhile shifts the pages by one, so the first one can repeat
      setWorkouts((prev) => [
        ...prev,
        ...next.workouts.filter((w) => !prev.some((p) => p.id === w.id)),
      ]);
      setHasNext(next.hasNext);
      setPage(page + 1);
    } catch (err) {
      setError(fallbackErrorMessage(err));
    } finally {
      setLoading(false);
    }
  }

  if (workouts.length === 0) {
    return (
      <div className="mt-4 rounded-card bg-surface px-4 pt-5 pb-4">
        <h2 className="font-semibold">No workouts yet</h2>
        <p className="mt-1 text-meta text-text-secondary">
          Finished workouts show up here, newest first.
        </p>
      </div>
    );
  }

  return (
    <>
      {groupByWeek(workouts).map((group) => (
        <section key={group.key} className="mt-6" aria-labelledby={`week-${group.key}`}>
          <h2
            id={`week-${group.key}`}
            className="mb-2 text-meta font-semibold text-text-secondary"
          >
            {group.label}
          </h2>
          <ul className={listClass}>
            {group.workouts.map((workout) => (
              <li key={workout.id}>
                <Link href={`/history/${workout.id}`} className={listRowClass}>
                  <span className="min-w-0 flex-1">
                    <span className="block truncate font-semibold">{workout.name}</span>
                    <span className="block text-meta text-text-secondary tabular-nums">
                      {dayFormat.format(new Date(workout.startedAt))},{' '}
                      {formatDuration(workout.startedAt, workout.endedAt)},{' '}
                      {workout.setCount} {workout.setCount === 1 ? 'set' : 'sets'}
                    </span>
                    <span className="block truncate text-meta text-text-tertiary">
                      {workout.exerciseNames.join(', ')}
                    </span>
                  </span>
                  <ChevronRight
                    className="size-5 shrink-0 text-text-tertiary"
                    aria-hidden="true"
                  />
                </Link>
              </li>
            ))}
          </ul>
        </section>
      ))}

      {error && (
        <p role="alert" className="mt-4 text-meta text-danger">
          {error}
        </p>
      )}
      {hasNext && (
        <Button
          type="button"
          variant="text"
          className="mt-4 w-full"
          disabled={loading}
          onClick={loadMore}
        >
          {loading ? 'Loading...' : 'Load More'}
        </Button>
      )}
    </>
  );
}
