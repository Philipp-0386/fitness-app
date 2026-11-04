import { redirect } from 'next/navigation';

import { fetchActiveWorkout, WorkoutTopBar } from '@/features/workouts';
import { requireSession } from '@/shared/auth/session';

export default async function WorkoutPage() {
  await requireSession();

  const workout = await fetchActiveWorkout();
  if (!workout) redirect('/dashboard');

  return (
    <>
      <WorkoutTopBar />
      <main className="flex flex-1 flex-col gap-4 p-4">
        <h1 className="text-display">{workout.name}</h1>
        <ol>
          {workout.exercises.map((entry) => (
            <li key={entry.id}>{entry.exercise.name}</li>
          ))}
        </ol>
      </main>
    </>
  );
}
