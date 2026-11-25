import Link from 'next/link';
import { useTranslations } from 'next-intl';

import { Button } from '@/shared/ui/button';
import { LanguageSwitch } from '@/shared/ui/language-switch';

const features = [
  { key: 'plan', available: true },
  { key: 'log', available: false },
  { key: 'progress', available: false },
] as const;

export default function GuestHome() {
  const t = useTranslations('guestHome');

  return (
    <main className="mx-auto flex w-full max-w-app flex-1 flex-col px-4">
      <header className="flex h-14 items-center justify-between">
        <p className="text-title">Avren</p>
        <LanguageSwitch />
      </header>

      <section className="pt-4">
        <h1 className="text-display text-balance">{t('title')}</h1>
        <p className="mt-3 text-text-secondary">{t('intro')}</p>
      </section>

      <section className="mt-6" aria-labelledby="features-title">
        <h2 id="features-title" className="mb-3 text-title">
          {t('featuresTitle')}
        </h2>
        <ul className="rounded-card bg-surface">
          {features.map(({ key, available }) => (
            <li
              key={key}
              className="border-b border-border-subtle px-4 py-3 last:border-b-0"
            >
              <div className="flex items-baseline justify-between gap-3">
                <h3 className="font-semibold">{t(`features.${key}.title`)}</h3>
                <span
                  className={
                    available
                      ? 'shrink-0 text-meta text-text-secondary'
                      : 'shrink-0 text-meta text-text-tertiary'
                  }
                >
                  {available ? t('available') : t('inDevelopment')}
                </span>
              </div>
              <p className="mt-0.5 text-meta text-pretty text-text-tertiary">
                {t(`features.${key}.text`)}
              </p>
            </li>
          ))}
        </ul>
      </section>

      <div className="sticky bottom-0 mt-auto bg-bg pt-4 pb-4 sm:static sm:mt-0 sm:pt-8">
        <Button asChild variant="primary" size="lg">
          <Link href="/login">{t('logIn')}</Link>
        </Button>
        <p className="mt-4 text-center text-meta text-text-secondary">
          {t('noAccount')}{' '}
          <Link
            href="/signup"
            className="relative font-semibold text-text-primary underline underline-offset-2 before:absolute before:-inset-x-2 before:-inset-y-3.5"
          >
            {t('signUp')}
          </Link>
        </p>
      </div>
    </main>
  );
}
