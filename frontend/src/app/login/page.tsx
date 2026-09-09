import { redirect } from 'next/navigation';

import { LoginForm } from '@/features/login/index';
import { isAuthenticated } from '@/shared/auth/session';

export default async function LoginPage() {
  if (await isAuthenticated()) {
    redirect('/dashboard');
  }

  return (
    <div>
      <h1>Log In</h1>
      <LoginForm />
    </div>
  );
}
