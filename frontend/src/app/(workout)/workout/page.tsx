import { redirect } from 'next/navigation';

import { fetchExercises, fetchMuscleGroups } from '@/features/exercises';
import { fetchActiveWorkout, ActiveWorkoutView } from '@/features/workouts';
import { requireSession } from '@/shared/auth/session';

export default async function WorkoutPage() {
  await requireSession();

  const [workout, catalog, muscleGroups] = await Promise.all([
    fetchActiveWorkout(),
    fetchExercises().catch(() => []),
    fetchMuscleGroups().catch(() => []),
  ]);
  if (!workout) redirect('/dashboard');

  return (
    <>
      <ActiveWorkoutView
        workout={workout}
        catalog={catalog}
        muscleGroups={muscleGroups}
      />
    </>
  );
}
