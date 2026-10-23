import { fetchExercises } from '@/features/exercises';
import { RoutineEditor } from '@/features/routines';
import { requireSession } from '@/shared/auth/session';

export default async function NewRoutinePage() {
  await requireSession();

  const exercises = await fetchExercises();

  return <RoutineEditor routine={null} exercises={exercises} />;
}
