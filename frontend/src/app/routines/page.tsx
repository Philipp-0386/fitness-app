import { CreateRoutineForm, RoutineJsonView, RoutineSource } from '@/features/routines';
import { requireSession } from '@/shared/auth/session';

export default async function RoutinesPage({
  searchParams,
}: {
  searchParams: Promise<{ id?: string; source?: string }>;
}) {
  await requireSession();

  const { id, source } = await searchParams;
  const resolvedSource: RoutineSource = source === 'presets' ? 'presets' : 'mine';

  return (
    <main>
      <h1>Routines</h1>

      <form method="get">
        <label>
          source:{' '}
          <select name="source" defaultValue={resolvedSource}>
            <option value="mine">mine</option>
            <option value="presets">presets</option>
          </select>
        </label>{' '}
        <label>
          id: <input name="id" defaultValue={id ?? ''} />
        </label>
        <button type="submit">load</button>
      </form>
      <p>
        <a href="/routines">clear (load your own routines)</a>
      </p>

      {/* Seeded ids from V2 reference data and V9001 dev data */}
      <p>
        ids 1-3 presets, 4 and 6-9 max, 5 lena_lifts. Presets answer only under presets,
        own routines only under mine, anything else answers 404.
      </p>

      <RoutineJsonView source={resolvedSource} id={id} />

      <CreateRoutineForm />
    </main>
  );
}
