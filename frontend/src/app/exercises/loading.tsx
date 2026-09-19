/**
 * Shown while the server component of this route awaits the backend.
 *
 * The page itself renders immediately - only the part that reads exercises is replaced by
 * this fallback, so the form stays usable while a request is in flight.
 */
export default function Loading() {
  return <p>Loading exercises...</p>;
}
