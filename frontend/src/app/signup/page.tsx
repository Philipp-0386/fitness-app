import { SignUpClosedBanner, SignUpForm } from '@/features/signup';
import { redirectIfAuthenticated } from '@/shared/auth/session';

export default async function SignUpPage() {
  await redirectIfAuthenticated();

  return (
    <div>
      <h1>Sign Up Test Page</h1>
      <SignUpClosedBanner />
      <SignUpForm />
    </div>
  );
}
