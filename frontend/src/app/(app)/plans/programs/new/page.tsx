import { ProgramEditor } from '@/features/programs';
import { fetchUserRoutines } from '@/features/routines';
import { requireSession } from '@/shared/auth/session';

export default async function NewProgramPage() {
  await requireSession();

  const routines = await fetchUserRoutines();

  return <ProgramEditor routines={routines} />;
}
