'use client';

import { Button } from '@/shared/ui/button';

type Props = {
  error: Error & { digest?: string };
  reset: () => void;
};

/**
 * Catches anything the detail page throws, such as an ApiError from fetchExerciseById that
 * nothing else handled. The catalog in the layout handles its own errors.
 *
 * The error arrives here as a plain Error: in a production build Next replaces the message
 * with a generic one and only keeps `digest` to match it against the server log, so this
 * must not rely on ApiError fields.
 */
export default function ExerciseError({ error, reset }: Props) {
  return (
    <div className="mx-auto flex w-full max-w-app flex-1 flex-col pb-8 lg:max-w-[560px]">
      <header className="flex min-h-tap items-center px-4 pt-4">
        <h1 className="text-display">Exercise</h1>
      </header>

      <div className="px-4 pt-3">
        <section role="alert" className="rounded-card bg-surface px-4 pt-5 pb-4">
          <h2 className="text-title">This exercise could not be loaded</h2>
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
