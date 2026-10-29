import Link from 'next/link';
import { ChevronRight, UserRound } from 'lucide-react';

import {
  ExerciseFormDialog,
  fetchExercises,
  fetchMuscleGroups,
} from '@/features/exercises';
import { ProgramList, fetchUserPrograms } from '@/features/programs';
import { RoutineList, fetchUserRoutines } from '@/features/routines';
import { fetchMe } from '@/features/user';
import { requireSession } from '@/shared/auth/session';
import { ListPane } from '@/shared/layout';
import { Button } from '@/shared/ui/button';
import { listClass, listRowClass } from '@/shared/ui/list';

// Same limit as ProgramService, the backend still rejects a sixth program.
const MAX_PROGRAMS = 5;

export default async function PlansLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  await requireSession();

  const [plans, username] = await Promise.all([
    Promise.all([
      fetchUserPrograms(),
      fetchUserRoutines(),
      fetchExercises(),
      fetchMuscleGroups(),
    ])
      .then(([programs, routines, exercises, muscleGroups]) => ({
        programs,
        routines,
        exercises,
        muscleGroups,
      }))
      .catch((error: Error) => error),
    fetchMe()
      .then((user) => user.username)
      .catch(() => null),
  ]);

  return (
    <main className="flex flex-1 flex-col lg:grid lg:grid-cols-[minmax(320px,400px)_minmax(0,1fr)]">
      <ListPane>
        <div className="mx-auto w-full max-w-app px-4 pb-8">
          <header className="flex items-center justify-between pt-4 lg:pt-8">
            <h1 className="text-display">Plans</h1>
            <Link
              href="/me"
              aria-label="Account"
              className="grid size-tap place-items-center rounded-full bg-surface-raised font-semibold lg:hidden"
            >
              {username ? (
                username[0].toUpperCase()
              ) : (
                <UserRound className="size-5" aria-hidden="true" />
              )}
            </Link>
          </header>

          {plans instanceof Error ? (
            <section role="alert" className="mt-4 rounded-card bg-surface px-4 pt-5 pb-4">
              <h2 className="text-title">Your plans could not be loaded</h2>
              <p className="mt-1 text-text-secondary">{plans.message}</p>
              <p className="mt-1 text-meta text-text-tertiary">
                Reload the page to try again.
              </p>
            </section>
          ) : (
            <>
              <section className="mt-4" aria-labelledby="programs-title">
                <div className="flex items-center justify-between gap-3">
                  <h2 id="programs-title" className="text-title">
                    Programs
                  </h2>
                  {plans.programs.length < MAX_PROGRAMS ? (
                    <Button asChild size="sm">
                      <Link href="/plans/programs/new">New Program</Link>
                    </Button>
                  ) : (
                    <Button size="sm" disabled aria-describedby="programs-limit">
                      New Program
                    </Button>
                  )}
                </div>
                {plans.programs.length < MAX_PROGRAMS ? (
                  <p className="mt-0.5 mb-3 text-meta text-text-secondary">
                    Create your own training plan.
                  </p>
                ) : (
                  <p
                    id="programs-limit"
                    className="mt-0.5 mb-3 text-meta text-text-secondary"
                  >
                    You have reached the maximum of {MAX_PROGRAMS} programs. Delete one to
                    create a new one.
                  </p>
                )}
                {plans.programs.length > 0 ? (
                  <ProgramList programs={plans.programs} />
                ) : (
                  <div className="rounded-card bg-surface px-4 pt-5 pb-4">
                    <h3 className="font-semibold">No programs yet</h3>
                    <p className="mt-1 text-meta text-text-secondary">
                      Once you have a program, the active one shows its days on Today.
                    </p>
                  </div>
                )}
              </section>

              <section className="mt-8" aria-labelledby="routines-title">
                <div className="flex items-center justify-between gap-3">
                  <h2 id="routines-title" className="text-title">
                    Routines
                  </h2>
                  <Button asChild size="sm">
                    <Link href="/plans/routines/new">New Routine</Link>
                  </Button>
                </div>
                <p className="mt-0.5 mb-3 text-meta text-text-secondary">
                  Exercises and targets for a single training day.
                </p>
                {plans.routines.length > 0 ? (
                  <RoutineList routines={plans.routines} hrefBase="/plans/routines" />
                ) : (
                  <div className="rounded-card bg-surface px-4 pt-5 pb-4">
                    <h3 className="font-semibold">No routines yet</h3>
                    <p className="mt-1 text-meta text-text-secondary">
                      Create your first routine, from scratch or from a preset.
                    </p>
                  </div>
                )}
              </section>

              <section className="mt-8" aria-labelledby="exercises-title">
                <div className="flex items-center justify-between gap-3">
                  <h2 id="exercises-title" className="text-title">
                    Exercises
                  </h2>
                  <ExerciseFormDialog muscleGroups={plans.muscleGroups} />
                </div>
                <p className="mt-0.5 mb-3 text-meta text-text-secondary">
                  Add your own when the catalog is missing one.
                </p>
                <ul className={listClass}>
                  <li>
                    <Link href="/exercises" className={listRowClass}>
                      <span className="min-w-0 flex-1">
                        <span className="block font-semibold">Exercise catalog</span>
                        <span className="block text-meta text-text-tertiary tabular-nums">
                          {plans.exercises.length} exercises,{' '}
                          {plans.exercises.filter((exercise) => exercise.custom).length}{' '}
                          of them yours
                        </span>
                      </span>
                      <ChevronRight
                        className="size-5 shrink-0 text-text-tertiary"
                        aria-hidden="true"
                      />
                    </Link>
                  </li>
                </ul>
              </section>
            </>
          )}
        </div>
      </ListPane>

      {children}
    </main>
  );
}
