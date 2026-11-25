import Link from 'next/link';
import { ChevronLeft } from 'lucide-react';
import { getTranslations } from 'next-intl/server';

import { isAuthenticated } from '@/shared/auth/session';
import { GITHUB_ISSUES_URL, GITHUB_URL } from '@/shared/site';

const linkClass = 'font-semibold text-text-primary underline underline-offset-2';

export default async function AboutContent() {
  const loggedIn = await isAuthenticated();
  const t = await getTranslations('about');
  const tNav = await getTranslations('nav');

  return (
    <main className="mx-auto flex w-full max-w-app flex-1 flex-col px-4 pb-8">
      <header className="flex h-14 items-center">
        <Link
          href={loggedIn ? '/dashboard' : '/'}
          className="-ml-2 flex min-h-tap items-center gap-0.5 pr-2 font-semibold text-text-secondary hover:text-text-primary"
        >
          <ChevronLeft className="size-6" aria-hidden="true" />
          {loggedIn ? tNav('today') : tNav('home')}
        </Link>
      </header>

      <h1 className="pt-6 pb-3 text-display">{t('title')}</h1>

      <p className="text-text-secondary">{t('intro')}</p>

      <section className="mt-6 rounded-card bg-surface px-4 pt-5 pb-4">
        <h2 className="mb-3 text-title">{t('noteTitle')}</h2>
        <ul className="flex list-disc flex-col gap-2 pl-5 text-text-secondary">
          <li>{t('noteReset')}</li>
          <li>{t('noteFree')}</li>
          <li>
            {t.rich('noteAdvice', {
              strong: (chunks) => (
                <strong className="font-semibold text-text-primary">{chunks}</strong>
              ),
            })}
          </li>
        </ul>
      </section>

      <p className="mt-6 text-text-secondary">
        {t.rich('feedback', {
          link: (chunks) => (
            <a
              href={GITHUB_ISSUES_URL}
              className={linkClass}
              target="_blank"
              rel="noopener noreferrer"
            >
              {chunks}
            </a>
          ),
        })}
      </p>

      <p className="mt-3 text-text-secondary">
        {t.rich('source', {
          link: (chunks) => (
            <a
              href={GITHUB_URL}
              className={linkClass}
              target="_blank"
              rel="noopener noreferrer"
            >
              {chunks}
            </a>
          ),
        })}
      </p>
    </main>
  );
}
