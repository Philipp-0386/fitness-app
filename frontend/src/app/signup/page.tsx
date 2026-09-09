import { redirect } from 'next/navigation';

import { SignUpForm } from '@/features/signup/index';
import { isAuthenticated } from '@/shared/auth/session';

export default async function SignUpPage() {
  if (await isAuthenticated()) {
    redirect('/dashboard');
  }

  return (
    <div>
      <h1>Sign Up Test Page</h1>
      <SignUpForm />
    </div>
  );
}
