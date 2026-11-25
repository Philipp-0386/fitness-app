import { getTranslations } from 'next-intl/server';

import { requireSession } from '@/shared/auth/session';

export default async function ProgressPage() {
  await requireSession();
  const t = await getTranslations('progress');

  return (
    <main className="mx-auto flex w-full max-w-app flex-1 flex-col px-4 pt-4 pb-8">
      <h1 className="text-display">{t('title')}</h1>
      <p className="mt-1 text-text-secondary">{t('text')}</p>
    </main>
  );
}
