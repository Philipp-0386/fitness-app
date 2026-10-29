import { UserHome } from '@/features/home';
import { fetchActiveProgram } from '@/features/programs';
import { fetchMe } from '@/features/user';
import { requireSession } from '@/shared/auth/session';

export default async function DashboardPage() {
  await requireSession();

  const [username, program] = await Promise.all([
    fetchMe()
      .then((user) => user.username)
      .catch(() => null),
    fetchActiveProgram().catch((error: Error) => error),
  ]);

  return <UserHome username={username} program={program} />;
}
