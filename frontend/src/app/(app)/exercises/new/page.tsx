import { ExerciseEditor, fetchMuscleGroups } from '@/features/exercises';
import { requireSession } from '@/shared/auth/session';

export default async function NewExercisePage() {
  await requireSession();

  const muscleGroups = await fetchMuscleGroups();

  return <ExerciseEditor exercise={null} muscleGroups={muscleGroups} />;
}
