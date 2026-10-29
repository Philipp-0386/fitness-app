import {
  ExerciseCatalog,
  ExerciseFormDialog,
  fetchExercises,
  fetchMuscleGroups,
} from '@/features/exercises';
import { requireSession } from '@/shared/auth/session';
import { ListPane, TopBar } from '@/shared/layout';

export default async function ExercisesLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  await requireSession();

  const catalog = await Promise.all([fetchExercises(), fetchMuscleGroups()])
    .then(([exercises, muscleGroups]) => ({ exercises, muscleGroups }))
    .catch((error: Error) => error);

  return (
    <main className="flex flex-1 flex-col lg:grid lg:grid-cols-[minmax(320px,400px)_minmax(0,1fr)]">
      <ListPane>
        <div className="mx-auto w-full max-w-app pb-8">
          <TopBar
            backHref="/plans"
            backLabel="Plans"
            title="Exercises"
            titleAction={
              !(catalog instanceof Error) && (
                <ExerciseFormDialog muscleGroups={catalog.muscleGroups} />
              )
            }
          />
          {catalog instanceof Error ? (
            <section
              role="alert"
              className="mx-4 mt-4 rounded-card bg-surface px-4 pt-5 pb-4"
            >
              <h2 className="text-title">The exercise catalog could not be loaded</h2>
              <p className="mt-1 text-text-secondary">{catalog.message}</p>
              <p className="mt-1 text-meta text-text-tertiary">
                Reload the page to try again.
              </p>
            </section>
          ) : (
            <ExerciseCatalog
              exercises={catalog.exercises}
              muscleGroups={catalog.muscleGroups}
            />
          )}
        </div>
      </ListPane>

      {children}
    </main>
  );
}
