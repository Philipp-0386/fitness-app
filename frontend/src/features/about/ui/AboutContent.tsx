import Link from 'next/link';
import { ChevronLeft } from 'lucide-react';

import { isAuthenticated } from '@/shared/auth/session';
import { GITHUB_ISSUES_URL, GITHUB_URL } from '@/shared/site';

const linkClass = 'font-semibold text-text-primary underline underline-offset-2';

export default async function AboutContent() {
  const loggedIn = await isAuthenticated();

  return (
    <main className="mx-auto flex w-full max-w-app flex-1 flex-col px-4 pb-8">
      <header className="flex h-14 items-center">
        <Link
          href={loggedIn ? '/dashboard' : '/'}
          className="-ml-2 flex min-h-tap items-center gap-0.5 pr-2 font-semibold text-text-secondary hover:text-text-primary"
        >
          <ChevronLeft className="size-6" aria-hidden="true" />
          {loggedIn ? 'Today' : 'Home'}
        </Link>
      </header>

      <h1 className="pt-6 pb-3 text-display">About this app</h1>

      <p className="text-text-secondary">
        This app is a personal project. It is still in early development, so expect rough
        edges, missing features and the occasional bug.
      </p>

      <section className="mt-6 rounded-card bg-surface px-4 pt-5 pb-4">
        <h2 className="mb-3 text-title">Please note</h2>
        <ul className="flex list-disc flex-col gap-2 pl-5 text-text-secondary">
          <li>
            Your data may be reset while the app is still evolving. Do not rely on it as
            your only training log for now.
          </li>
          <li>The app is free and non-commercial. There are no ads and no tracking.</li>
          <li>
            This is{' '}
            <strong className="font-semibold text-text-primary">
              not medical or professional
            </strong>{' '}
            training advice.
          </li>
        </ul>
      </section>

      <p className="mt-6 text-text-secondary">
        Found a bug or have an idea? I&apos;d love to hear it:{' '}
        <a
          href={GITHUB_ISSUES_URL}
          className={linkClass}
          target="_blank"
          rel="noopener noreferrer"
        >
          open an issue on GitHub
        </a>
        .
      </p>

      <p className="mt-3 text-text-secondary">
        The source code is available on{' '}
        <a
          href={GITHUB_URL}
          className={linkClass}
          target="_blank"
          rel="noopener noreferrer"
        >
          GitHub
        </a>
        .
      </p>
    </main>
  );
}
