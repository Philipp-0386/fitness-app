import { notFound } from 'next/navigation';

import { ExerciseDetailView, fetchExerciseById } from '@/features/exercises';
import { requireSession } from '@/shared/auth/session';
import { ApiError } from '@/shared/api/errors/api-error';

export default async function ExercisePage({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  await requireSession();

  const { id } = await params;
  const exercise = await fetchExerciseById(id).catch((error) => {
    if (error instanceof ApiError && error.status === 404) notFound();
    throw error;
  });

  return <ExerciseDetailView exercise={exercise} />;
}
