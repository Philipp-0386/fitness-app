import Link from 'next/link';
import { ChevronRight, UserRound } from 'lucide-react';

type UserHomeProps = {
  username: string | null;
};

export default function UserHome({ username }: UserHomeProps) {
  const links = [
    {
      title: 'Routines',
      text: 'Build and edit your training plans',
      href: '/routines',
    },
    {
      title: 'Exercises',
      text: 'Browse the exercise catalog',
      href: '/exercises',
    },
    {
      title: 'Account',
      text: username ? `Signed in as ${username}` : 'Profile, password and log out',
      href: '/me',
    },
  ];

  return (
    <main className="mx-auto flex w-full max-w-app flex-1 flex-col px-4 pb-8">
      <header className="flex items-center justify-between pt-4">
        <h1 className="text-display">Today</h1>
        <Link
          href="/me"
          aria-label="Account"
          className="grid size-tap place-items-center rounded-full bg-surface-raised font-semibold lg:hidden"
        >
          {username ? (
            username[0].toUpperCase()
          ) : (
            <UserRound className="size-5" aria-hidden="true" />
          )}
        </Link>
      </header>

      <section
        className="mt-3 rounded-card bg-surface px-4 pt-5 pb-4"
        aria-labelledby="status-title"
      >
        <h2 id="status-title" className="text-title">
          Logging is coming
        </h2>
        <p className="mt-1 text-text-secondary">
          Sessions, history and progress are in development. For now you can build
          routines from the exercise catalog.
        </p>
      </section>

      <nav aria-label="Sections" className="mt-8">
        <ul className="divide-y divide-border-subtle overflow-hidden rounded-card bg-surface">
          {links.map(({ title, text, href }) => (
            <li key={title}>
              <Link
                href={href}
                className="flex min-h-14 items-center gap-3 px-4 py-2.5 hover:bg-surface-raised focus-visible:-outline-offset-2 active:bg-surface-raised"
              >
                <span className="min-w-0 flex-1">
                  <span className="block font-semibold">{title}</span>
                  <span className="block text-meta text-text-tertiary">{text}</span>
                </span>
                <ChevronRight
                  className="size-5 shrink-0 text-text-tertiary"
                  aria-hidden="true"
                />
              </Link>
            </li>
          ))}
        </ul>
      </nav>
    </main>
  );
}
