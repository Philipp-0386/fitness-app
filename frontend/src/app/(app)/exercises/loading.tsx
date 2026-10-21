/**
 * Shown while the server component of this route awaits the backend.
 */
export default function Loading() {
  return (
    <main className="mx-auto flex w-full max-w-app flex-1 flex-col px-4 pt-4 pb-8 lg:max-w-[720px]">
      <h1 className="pt-14 text-display">Exercises</h1>
      <p className="mt-1 text-text-secondary">Loading exercises...</p>
    </main>
  );
}
