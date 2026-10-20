import { UserHome } from '@/features/home';
import { fetchMe } from '@/features/user';
import { requireSession } from '@/shared/auth/session';

export default async function DashboardPage() {
  await requireSession();

  const username = await fetchMe()
    .then((user) => user.username)
    .catch(() => null);

  return <UserHome username={username} />;
}
