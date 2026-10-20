import { requireSession } from '@/shared/auth/session';

export default async function ProgressPage() {
  await requireSession();

  return (
    <main className="mx-auto flex w-full max-w-app flex-1 flex-col px-4 pt-4 pb-8">
      <h1 className="text-display">Progress</h1>
      <p className="mt-1 text-text-secondary">
        In development. Records, volume and progression will show up here.
      </p>
    </main>
  );
}
