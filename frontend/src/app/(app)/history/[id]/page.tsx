import { notFound } from 'next/navigation';

import { WorkoutDetailView, fetchWorkoutById } from '@/features/workouts';
import { ApiError } from '@/shared/api/errors/api-error';
import { requireSession } from '@/shared/auth/session';

export default async function WorkoutPage({
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

  return <WorkoutDetailView workout={workout} />;
}
