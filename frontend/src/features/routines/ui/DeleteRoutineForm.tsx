'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { ApiError } from '@/shared/api/errors/api-error';
import { fallbackErrorMessage } from '@/shared/api/errors/fallback-error-message';
import { deleteRoutine } from '../client/delete-routine.client';

/**
 * Minimal form to delete an own routine by id, the result or the error is shown as JSON.
 */
export default function DeleteRoutineForm() {
  const [id, setId] = useState('');
  const [isDeleting, setIsDeleting] = useState(false);
  const [payload, setPayload] = useState<string | null>(null);
  const router = useRouter();

  async function handleSubmit(e: React.SubmitEvent<HTMLFormElement>) {
    e.preventDefault();
    if (isDeleting) return;

    setIsDeleting(true);
    try {
      await deleteRoutine(id);
      setPayload(`routine ${id} deleted`);
      router.refresh();
    } catch (err) {
      if (err instanceof ApiError) {
        setPayload(
          JSON.stringify(
            { status: err.status, code: err.code, message: err.message },
            null,
            2,
          ),
        );
      } else {
        setPayload(fallbackErrorMessage(err));
      }
    } finally {
      setIsDeleting(false);
    }
  }

  return (
    <section>
      <h2>delete routine</h2>
      <form onSubmit={handleSubmit} noValidate>
        <label>
          id: <input value={id} onChange={(e) => setId(e.target.value)} />
        </label>{' '}
        <button type="submit" disabled={isDeleting}>
          {isDeleting ? 'deleting...' : 'delete'}
        </button>
      </form>
      {payload && <pre>{payload}</pre>}
    </section>
  );
}
