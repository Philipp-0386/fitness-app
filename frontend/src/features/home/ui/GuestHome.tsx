import Link from 'next/link';

import { Button } from '@/shared/ui/button';

const features = [
  {
    title: 'Plan your workouts',
    text: 'Build routines from an exercise catalog and reuse them week after week.',
    available: true,
  },
  {
    title: 'Log every session',
    text: 'Track sets, reps and weights while you train.',
    available: false,
  },
  {
    title: 'See your progress',
    text: 'Watch your volume and personal records develop over time.',
    available: false,
  },
];

export default function GuestHome() {
  return (
    <main className="mx-auto flex w-full max-w-app flex-1 flex-col px-4">
      <header className="flex h-14 items-center">
        <p className="text-title">Fitness App</p>
      </header>

      <section className="pt-6">
        <h1 className="text-display text-balance">Train with a plan.</h1>
        <p className="mt-3 text-text-secondary">
          Create your training plans, log your sessions and keep track of your progress in
          one place.
        </p>
      </section>

      <section className="mt-8" aria-labelledby="features-title">
        <h2 id="features-title" className="mb-3 text-title">
          What it does
        </h2>
        <ul className="rounded-card bg-surface">
          {features.map(({ title, text, available }) => (
            <li
              key={title}
              className="border-b border-border-subtle px-4 py-3 last:border-b-0"
            >
              <div className="flex items-baseline justify-between gap-3">
                <h3 className="font-semibold">{title}</h3>
                <span
                  className={
                    available
                      ? 'shrink-0 text-meta text-text-secondary'
                      : 'shrink-0 text-meta text-text-tertiary'
                  }
                >
                  {available ? 'Available' : 'In development'}
                </span>
              </div>
              <p className="mt-0.5 text-meta text-pretty text-text-tertiary">{text}</p>
            </li>
          ))}
        </ul>
      </section>

      <div className="sticky bottom-0 mt-auto bg-bg pt-8 pb-6">
        <Button asChild variant="primary" size="lg">
          <Link href="/login">Log in</Link>
        </Button>
        <Button asChild variant="secondary" size="lg" className="mt-3">
          <Link href="/signup">Sign up</Link>
        </Button>
        <p className="mt-3 text-center text-meta text-text-tertiary">
          Sign-ups are closed. Accounts are created on invitation.
        </p>
      </div>
    </main>
  );
}
