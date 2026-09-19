import { LoginForm } from '@/features/login';
import { redirectIfAuthenticated } from '@/shared/auth/session';

export default async function LoginPage() {
  await redirectIfAuthenticated();

  return (
    <div>
      <h1>Log In</h1>
      <LoginForm />
    </div>
  );
}
