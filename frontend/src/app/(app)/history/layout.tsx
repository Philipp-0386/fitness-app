import { getTranslations } from 'next-intl/server';

import { HistoryList, fetchWorkoutHistory } from '@/features/workouts';
import { getErrorMessage } from '@/shared/api/errors/get-error-message';
import { requireSession } from '@/shared/auth/session';
import { ListPane } from '@/shared/layout';

export default async function HistoryLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  await requireSession();
  const t = await getTranslations('workouts.history');
  const tCommon = await getTranslations('common');

  const history = await fetchWorkoutHistory().catch((error: Error) => error);

  return (
    <main className="flex flex-1 flex-col lg:grid lg:grid-cols-[minmax(320px,400px)_minmax(0,1fr)]">
      <ListPane>
        <div className="mx-auto w-full max-w-app px-4 pb-8">
          <h1 className="pt-4 text-display lg:pt-8">{t('title')}</h1>
          {history instanceof Error ? (
            <section role="alert" className="mt-4 rounded-card bg-surface px-4 pt-5 pb-4">
              <h2 className="text-title">{t('loadError')}</h2>
              <p className="mt-1 text-text-secondary">{await getErrorMessage(history)}</p>
              <p className="mt-1 text-meta text-text-tertiary">{tCommon('reload')}</p>
            </section>
          ) : (
            <HistoryList initial={history} />
          )}
        </div>
      </ListPane>

      {children}
    </main>
  );
}
