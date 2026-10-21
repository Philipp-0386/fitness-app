import { ExerciseCatalog, fetchExercises } from '@/features/exercises';
import { requireSession } from '@/shared/auth/session';
import { TopBar } from '@/shared/layout';

export default async function ExercisesPage() {
  await requireSession();

  const exercises = await fetchExercises();

  return (
    <main className="mx-auto flex w-full max-w-app flex-1 flex-col pb-8 lg:max-w-[720px]">
      <TopBar backHref="/routines" backLabel="Plans" title="Exercises" />
      <ExerciseCatalog exercises={exercises} />
    </main>
  );
}
