import { ExerciseJsonView } from '@/features/exercises';
import { requireSession } from '@/shared/auth/session';

export default async function ExercisesPage({
  searchParams,
}: {
  searchParams: Promise<{ id?: string }>;
}) {
  await requireSession();

  const { id } = await searchParams;

  return (
    <main>
      <h1>Exercises</h1>

      <form method="get">
        <label>
          id: <input name="id" defaultValue={id ?? ''} />
        </label>
        <button type="submit">load</button>
      </form>
      <p>
        <a href="/exercises">clear (load the whole catalog)</a>
      </p>

      {/* Seeded ids from main.sql */}
      <p>
        ids 1-63 global, 64 max, 65 lena_lifts, 66 marco, 67 sina (soft deleted). Anything
        but your own or a global one answers 404.
      </p>

      <ExerciseJsonView id={id} />
    </main>
  );
}
