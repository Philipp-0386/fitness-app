import { fetchExercises, fetchMuscleGroups } from '@/features/exercises';
import { RoutineEditor } from '@/features/routines';
import { requireSession } from '@/shared/auth/session';

export default async function NewRoutinePage() {
  await requireSession();

  const [exercises, muscleGroups] = await Promise.all([
    fetchExercises(),
    fetchMuscleGroups(),
  ]);

  return (
    <RoutineEditor routine={null} exercises={exercises} muscleGroups={muscleGroups} />
  );
}
