import { notFound, redirect } from 'next/navigation';

import { WorkoutSummaryView, fetchWorkoutById } from '@/features/workouts';
import { ApiError } from '@/shared/api/errors/api-error';
import { requireSession } from '@/shared/auth/session';

export default async function WorkoutSummaryPage({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  await requireSession();

  const { id } = await params;
  const workout = await fetchWorkoutById(id).catch((error) => {
    if (error instanceof ApiError && (error.status === 404 || error.status === 400))
      notFound();
    throw error;
  });
  if (workout.status === 'IN_PROGRESS') redirect('/workout');

  return <WorkoutSummaryView workout={workout} />;
}
