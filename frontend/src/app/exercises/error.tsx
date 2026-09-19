'use client';

type Props = {
  error: Error & { digest?: string };
  reset: () => void;
};

/**
 * Catches anything the server component of this route throws, such as an ApiError from
 * fetchExercises that nothing else handled.
 *
 * Client component by convention - an error boundary needs to run in the browser.
 *
 * The error arrives here as a plain Error: in a production build Next replaces the message
 * with a generic one and only keeps `digest` to match it against the server log, so this
 * must not rely on ApiError fields.
 */
export default function ExercisesError({ error, reset }: Props) {
  return (
    <main>
      <h1>Exercises</h1>
      <p>The exercise catalog could not be loaded.</p>
      <p>{error.message}</p>
      {error.digest && <p>Reference: {error.digest}</p>}
      <button type="button" onClick={reset}>
        Try again
      </button>
    </main>
  );
}
