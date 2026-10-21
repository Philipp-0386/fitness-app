'use client';

import { Button } from '@/shared/ui/button';

type Props = {
  error: Error & { digest?: string };
  reset: () => void;
};

export default function RoutineError({ error, reset }: Props) {
  return (
    <div className="mx-auto flex w-full max-w-app flex-1 flex-col pb-8 lg:max-w-[560px]">
      <header className="flex min-h-tap items-center px-4 pt-4">
        <h1 className="text-display">Routine</h1>
      </header>

      <div className="px-4 pt-3">
        <section role="alert" className="rounded-card bg-surface px-4 pt-5 pb-4">
          <h2 className="text-title">This routine could not be loaded</h2>
          <p className="mt-1 text-text-secondary">{error.message}</p>
          {error.digest && (
            <p className="mt-1 text-meta text-text-tertiary">Reference: {error.digest}</p>
          )}
        </section>
        <Button
          className="mt-4"
          type="button"
          variant="primary"
          size="lg"
          onClick={reset}
        >
          Try again
        </Button>
      </div>
    </div>
  );
}
