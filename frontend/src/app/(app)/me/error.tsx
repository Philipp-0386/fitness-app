'use client';

type Props = {
  error: Error & { digest?: string };
  reset: () => void;
};

export default function MeError({ error, reset }: Props) {
  return (
    <main>
      <h1>Your account</h1>
      <p>Your account could not be loaded.</p>
      <p>{error.message}</p>
      {error.digest && <p>Reference: {error.digest}</p>}
      <button type="button" onClick={reset}>
        Try again
      </button>
    </main>
  );
}
