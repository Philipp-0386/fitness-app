import { redirect } from 'next/navigation';

import { UserHome } from '@/features/home';
import { getSession } from '@/shared/auth/session';

export default async function DashboardPage() {
  const session = await getSession();

  if (!session) {
    redirect('/login');
  }

  return <UserHome username={session.username} />;
}
