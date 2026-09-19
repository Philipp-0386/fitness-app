import { UserHome } from '@/features/home';
import { requireSession } from '@/shared/auth/session';

export default async function DashboardPage() {
  const session = await requireSession();

  return <UserHome username={session.username} />;
}
