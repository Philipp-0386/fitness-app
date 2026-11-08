import { notFound } from 'next/navigation';

import { fetchExercises } from '@/features/exercises';
import { RoutineDetailView, fetchUserRoutineByRoutineId } from '@/features/routines';
import { requireSession } from '@/shared/auth/session';
import { ApiError } from '@/shared/api/errors/api-error';
import { StartWorkoutButton, fetchActiveWorkout } from '@/features/workouts';

export default async function RoutinePage({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  await requireSession();

  const { id } = await params;
  const [routine, exercises, activeWorkout] = await Promise.all([
    fetchUserRoutineByRoutineId(id).catch((error) => {
      if (error instanceof ApiError && error.status === 404) notFound();
      throw error;
    }),
    fetchExercises(),
    fetchActiveWorkout().catch(() => null),
  ]);

  return (
    <RoutineDetailView
      routine={routine}
      exercises={exercises}
      startAction={<StartWorkoutButton routineId={routine.id} running={activeWorkout} />}
    />
  );
}
