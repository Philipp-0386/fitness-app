import { GuestHome } from '@/features/home';
import { redirectIfAuthenticated } from '@/shared/auth/session';

export default async function HomePage() {
  await redirectIfAuthenticated();

  return <GuestHome />;
}
