import { HistoryList, fetchWorkoutHistory } from '@/features/workouts';
import { requireSession } from '@/shared/auth/session';
import { ListPane } from '@/shared/layout';

export default async function HistoryLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  await requireSession();

  const history = await fetchWorkoutHistory().catch((error: Error) => error);

  return (
    <main className="flex flex-1 flex-col lg:grid lg:grid-cols-[minmax(320px,400px)_minmax(0,1fr)]">
      <ListPane>
        <div className="mx-auto w-full max-w-app px-4 pb-8">
          <h1 className="pt-4 text-display lg:pt-8">History</h1>
          {history instanceof Error ? (
            <section role="alert" className="mt-4 rounded-card bg-surface px-4 pt-5 pb-4">
              <h2 className="text-title">Your history could not be loaded</h2>
              <p className="mt-1 text-text-secondary">{history.message}</p>
              <p className="mt-1 text-meta text-text-tertiary">
                Reload the page to try again.
              </p>
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
