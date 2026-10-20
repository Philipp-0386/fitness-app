import { LoginForm } from '@/features/login';
import { redirectIfAuthenticated } from '@/shared/auth/session';
import Link from 'next/link';
import { Button } from '@/shared/ui/button';

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

      <p className="mt-5 text-center text-meta text-text-secondary">
        No account yet?{' '}
        <Link
          href="/signup"
          className="relative font-semibold text-text-primary underline underline-offset-2 before:absolute before:-inset-x-2 before:-inset-y-3.5"
        >
          Sign up
        </Link>
      </p>
    </main>
  );
}
