import { redirect } from 'next/navigation';

import { GuestHome } from '@/features/home';
import { isAuthenticated } from '@/shared/auth/session';

export default async function HomePage() {
  if (await isAuthenticated()) {
    redirect('/dashboard');
  }

  return <GuestHome />;
}
