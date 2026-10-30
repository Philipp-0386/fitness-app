import { notFound } from 'next/navigation';

import { fetchExercises } from '@/features/exercises';
import { RoutineDetailView, fetchUserRoutineByRoutineId } from '@/features/routines';
import { requireSession } from '@/shared/auth/session';
import { ApiError } from '@/shared/api/errors/api-error';

export default async function RoutinePage({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  await requireSession();

  const { id } = await params;
  const [routine, exercises] = await Promise.all([
    fetchUserRoutineByRoutineId(id).catch((error) => {
      if (error instanceof ApiError && error.status === 404) notFound();
      throw error;
    }),
    fetchExercises(),
  ]);

  return <RoutineDetailView routine={routine} exercises={exercises} />;
}
