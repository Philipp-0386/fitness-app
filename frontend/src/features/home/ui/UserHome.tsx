import Link from 'next/link';
import { ChevronRight, UserRound } from 'lucide-react';
import { useTranslations } from 'next-intl';

import { ProgramDayList, type ProgramDetail } from '@/features/programs';
import type { Workout } from '@/features/workouts/types/api.types';
import ResumeCard from '@/features/workouts/ui/ResumeCard';
import { useErrorMessage } from '@/shared/api/errors/use-error-message';

type UserHomeProps = {
  username: string | null;
  /** null without an active program, an Error if it could not be loaded. */
  program: ProgramDetail | null | Error;
  activeWorkout: Workout | null;
};

export default function UserHome({ username, program, activeWorkout }: UserHomeProps) {
  const t = useTranslations('userHome');
  const tNav = useTranslations('nav');
  const errorMessage = useErrorMessage();

  const links = [
    {
      title: t('plans.title'),
      text: t('plans.text'),
      href: '/plans',
    },
    {
      title: t('exercises.title'),
      text: t('exercises.text'),
      href: '/exercises',
    },
    {
      title: t('account.title'),
      text: username ? t('account.signedInAs', { username }) : t('account.text'),
      href: '/me',
    },
  ];

  return (
    <main className="mx-auto flex w-full max-w-app flex-1 flex-col px-4 pb-8">
      <header className="flex items-center justify-between pt-4">
        <h1 className="text-display">{t('title')}</h1>
        <Link
          href="/me"
          aria-label={tNav('account')}
          className="grid size-tap place-items-center rounded-full bg-surface-raised font-semibold lg:hidden"
        >
          {username ? (
            username[0].toUpperCase()
          ) : (
            <UserRound className="size-5" aria-hidden="true" />
          )}
        </Link>
      </header>

      {activeWorkout && <ResumeCard workout={activeWorkout} />}

      {program instanceof Error ? (
        <section role="alert" className="mt-8" aria-labelledby="program-title">
          <h2 id="program-title" className="text-title">
            {t('programError')}
          </h2>
          <p className="mt-1 text-text-secondary">{errorMessage(program)}</p>
          <p className="mt-1 text-meta text-text-tertiary">{t('reload')}</p>
        </section>
      ) : (
        program && (
          <section className="mt-8" aria-labelledby="program-title">
            <h2 id="program-title" className="mb-3 text-title">
              {program.name}
            </h2>
            <ProgramDayList program={program} exercises="count" />
          </section>
        )
      )}

      <nav aria-label={t('sections')} className="mt-8">
        <ul className="divide-y divide-border-subtle overflow-hidden rounded-card bg-surface">
          {links.map(({ title, text, href }) => (
            <li key={href}>
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
