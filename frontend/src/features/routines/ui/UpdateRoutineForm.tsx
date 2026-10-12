'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { ApiError } from '@/shared/api/errors/api-error';
import { fallbackErrorMessage } from '@/shared/api/errors/fallback-error-message';
import { updateRoutine } from '../client/update-routine.client';

const exampleExercises = `[
  { "exerciseId": 1, "targetSets": 3, "targetRepsMin": 8, "targetRepsMax": 10 },
  { "exerciseId": 38, "targetSets": 3, "targetDurationSeconds": 45 }
]`;

/**
 * Minimal form to update an own routine. The body replaces the whole routine, so name,
 * description and the exercises as raw JSON describe its complete new state.
 */
export default function UpdateRoutineForm() {
  const [id, setId] = useState('');
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
      const updated = await updateRoutine(id, {
        name,
        description: description || null,
        exercises: parsedExercises,
      });
      setPayload(JSON.stringify(updated, null, 2));
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
      <h2>update routine</h2>
      <form onSubmit={handleSubmit} noValidate>
        <label>
          id: <input value={id} onChange={(e) => setId(e.target.value)} />
        </label>
        <br />
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
          exercises (JSON, replaces all, list order sets orderIndex):
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
          {isSaving ? 'updating...' : 'update'}
        </button>
      </form>
      {payload && <pre>{payload}</pre>}
    </section>
  );
}
