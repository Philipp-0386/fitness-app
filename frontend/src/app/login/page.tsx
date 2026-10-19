import { LoginForm } from '@/features/login';
import { redirectIfAuthenticated } from '@/shared/auth/session';
import Link from 'next/dist/client/link';

export default async function LoginPage() {
  await redirectIfAuthenticated();

  return (
    <main className="mx-auto flex w-full max-w-app flex-1 flex-col px-4">
      <header className="flex h-14 items-center">
        <Link href="/" className="-mx-2 flex min-h-tap items-center px-2 text-title">
          Fitness App
        </Link>
      </header>

      <h1 className="pt-6 pb-8 text-display">Log in</h1>
      <LoginForm />
    </main>
  );
}
