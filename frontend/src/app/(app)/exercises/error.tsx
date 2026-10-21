'use client';

import { Button } from '@/shared/ui/button';

type Props = {
  error: Error & { digest?: string };
  reset: () => void;
};

/**
 * Catches anything the server component of this route throws, such as an ApiError from
 * fetchExercises that nothing else handled.
 *
 * An error boundary has to run in the browser, so this is a client component.
 *
 * The error arrives here as a plain Error: in a production build Next replaces the message
 * with a generic one and only keeps `digest` to match it against the server log, so this
 * must not rely on ApiError fields.
 */
export default function ExercisesError({ error, reset }: Props) {
  return (
    <main className="mx-auto flex w-full max-w-app flex-1 flex-col pb-8 lg:max-w-[720px]">
      <header className="flex min-h-tap items-center px-4 pt-4">
        <h1 className="text-display">Exercises</h1>
      </header>

      <div className="px-4 pt-3">
        <section role="alert" className="rounded-card bg-surface px-4 pt-5 pb-4">
          <h2 className="text-title">The exercise catalog could not be loaded</h2>
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
    </main>
  );
}
