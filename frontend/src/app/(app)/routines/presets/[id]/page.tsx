import { notFound } from 'next/navigation';

import { RoutineDetailView, fetchPresetRoutineByRoutineId } from '@/features/routines';
import { requireSession } from '@/shared/auth/session';
import { ApiError } from '@/shared/api/errors/api-error';

export default async function PresetRoutinePage({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  await requireSession();

  const { id } = await params;
  const routine = await fetchPresetRoutineByRoutineId(id).catch((error) => {
    if (error instanceof ApiError && error.status === 404) notFound();
    throw error;
  });

  return <RoutineDetailView routine={routine} />;
}
