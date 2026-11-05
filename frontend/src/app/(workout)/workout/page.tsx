import { redirect } from 'next/navigation';

import { fetchActiveWorkout, ActiveWorkoutView } from '@/features/workouts';
import { requireSession } from '@/shared/auth/session';

export default async function WorkoutPage() {
  await requireSession();

  const workout = await fetchActiveWorkout();
  if (!workout) redirect('/dashboard');

  return (
    <>
      <ActiveWorkoutView workout={workout} />
    </>
  );
}
