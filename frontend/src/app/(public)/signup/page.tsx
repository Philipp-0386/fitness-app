import Link from 'next/link';

import { SignUpClosedBanner, SignUpForm } from '@/features/signup';
import { redirectIfAuthenticated } from '@/shared/auth/session';

export default async function SignUpPage() {
  await redirectIfAuthenticated();

  return (
    <main className="mx-auto flex w-full max-w-app flex-1 flex-col px-4 pb-6">
      <header className="flex h-14 items-center">
        <Link href="/" className="-mx-2 flex min-h-tap items-center px-2 text-title">
          Fitness App
        </Link>
      </header>

      <h1 className="pt-6 pb-8 text-display">Sign up</h1>
      <SignUpClosedBanner />
      <SignUpForm />

      <p className="mt-5 text-center text-meta text-text-secondary">
        Already have an account?{' '}
        <Link
          href="/login"
          className="relative font-semibold text-text-primary underline underline-offset-2 before:absolute before:-inset-x-2 before:-inset-y-3.5"
        >
          Log in
        </Link>
      </p>
    </main>
  );
}
