import { notFound } from 'next/navigation';

import { ProgramDetailView, fetchUserProgramByProgramId } from '@/features/programs';
import { requireSession } from '@/shared/auth/session';
import { ApiError } from '@/shared/api/errors/api-error';

export default async function ProgramPage({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  await requireSession();

  const { id } = await params;
  const program = await fetchUserProgramByProgramId(id).catch((error) => {
    if (error instanceof ApiError && error.status === 404) notFound();
    throw error;
  });

  return <ProgramDetailView program={program} />;
}
