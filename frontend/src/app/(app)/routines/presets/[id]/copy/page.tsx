import { notFound } from 'next/navigation';

import { fetchExercises } from '@/features/exercises';
import { RoutineEditor, fetchPresetRoutineByRoutineId } from '@/features/routines';
import { requireSession } from '@/shared/auth/session';
import { ApiError } from '@/shared/api/errors/api-error';

export default async function CopyPresetRoutinePage({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  await requireSession();

  const { id } = await params;
  const [preset, exercises] = await Promise.all([
    fetchPresetRoutineByRoutineId(id).catch((error) => {
      if (error instanceof ApiError && error.status === 404) notFound();
      throw error;
    }),
    fetchExercises(),
  ]);

  return <RoutineEditor routine={null} copyOf={preset} exercises={exercises} />;
}
