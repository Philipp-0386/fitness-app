'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { ApiError } from '@/shared/api/errors/api-error';
import { fallbackErrorMessage } from '@/shared/api/errors/fallback-error-message';
import { createRoutine } from '../client/create-routine.client';

const exampleExercises = `[
  { "exerciseId": 1, "targetSets": 3, "targetRepsMin": 8, "targetRepsMax": 10 },
  { "exerciseId": 38, "targetSets": 3, "targetDurationSeconds": 45 }
]`;

/**
 * Minimal form to create a routine. The exercises are entered as raw JSON, the
 * response or the error is shown the same way the JSON views do.
 */
export default function CreateRoutineForm() {
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [exercises, setExercises] = useState(exampleExercises);
  const [isSaving, setIsSaving] = useState(false);
  const [payload, setPayload] = useState<string | null>(null);
  const router = useRouter();

  async function handleSubmit(e: React.SubmitEvent<HTMLFormElement>) {
    e.preventDefault();
    if (isSaving) return;

    let parsedExercises;
    try {
      parsedExercises = exercises.trim() ? JSON.parse(exercises) : [];
    } catch {
      setPayload('exercises is not valid JSON');
      return;
    }

    setIsSaving(true);
    try {
      const created = await createRoutine({
        name,
        description: description || null,
        exercises: parsedExercises,
      });
      setPayload(JSON.stringify(created, null, 2));
      router.refresh();
    } catch (err) {
      if (err instanceof ApiError) {
        setPayload(
          JSON.stringify(
            {
              status: err.status,
              code: err.code,
              message: err.message,
              fieldErrors: err.fieldErrors,
            },
            null,
            2,
          ),
        );
      } else {
        setPayload(fallbackErrorMessage(err));
      }
    } finally {
      setIsSaving(false);
    }
  }

  return (
    <section>
      <h2>create routine</h2>
      <form onSubmit={handleSubmit} noValidate>
        <label>
          name: <input value={name} onChange={(e) => setName(e.target.value)} />
        </label>
        <br />
        <label>
          description:{' '}
          <input value={description} onChange={(e) => setDescription(e.target.value)} />
        </label>
        <br />
        <label>
          exercises (JSON, list order sets orderIndex):
          <br />
          <textarea
            value={exercises}
            onChange={(e) => setExercises(e.target.value)}
            rows={6}
            cols={80}
          />
        </label>
        <br />
        <button type="submit" disabled={isSaving}>
          {isSaving ? 'creating...' : 'create'}
        </button>
      </form>
      {payload && <pre>{payload}</pre>}
    </section>
  );
}
