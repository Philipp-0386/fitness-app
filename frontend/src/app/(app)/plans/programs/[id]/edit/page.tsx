import { notFound } from 'next/navigation';

import { ProgramEditor, fetchUserProgramByProgramId } from '@/features/programs';
import { fetchUserRoutines } from '@/features/routines';
import { requireSession } from '@/shared/auth/session';
import { ApiError } from '@/shared/api/errors/api-error';

export default async function EditProgramPage({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  await requireSession();

  const { id } = await params;
  const [program, routines] = await Promise.all([
    fetchUserProgramByProgramId(id).catch((error) => {
      if (error instanceof ApiError && error.status === 404) notFound();
      throw error;
    }),
    fetchUserRoutines(),
  ]);

  return <ProgramEditor program={program} routines={routines} />;
}
