'use client';

import { ChevronDown, CircleAlert, X } from 'lucide-react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useState } from 'react';
import { toast } from 'sonner';

import { ApiError } from '@/shared/api/errors/api-error';
import { fallbackErrorMessage } from '@/shared/api/errors/fallback-error-message';
import { Button } from '@/shared/ui/button';
import { Input } from '@/shared/ui/input';
import { Label } from '@/shared/ui/label';
import { createExercise } from '../client/create-exercise.client';
import { updateExercise } from '../client/update-exercise.client';
import { trackingOptions } from '../lib/tracking-options';
import { typeLabels } from '../lib/type-labels';
import {
  Exercise,
  ExerciseType,
  MuscleGroup,
  MuscleRole,
  TrackingType,
} from '../types/api.types';
import DeleteExerciseDialog from './DeleteExerciseDialog';
import MuscleGroupPicker from './MuscleGroupPicker';

// Same limit as the exercise requests, the backend still rejects an eleventh muscle.
const MAX_MUSCLES = 10;

const cancelClass =
  '-ml-2 flex min-h-tap items-center justify-self-start px-2 font-semibold text-text-secondary hover:text-text-primary';

const textareaClass =
  'w-full min-w-0 rounded-field border border-border-strong bg-surface-raised px-3 py-2.5 text-field text-text-primary placeholder:text-text-tertiary';

const muscleRows: { role: MuscleRole; label: string; pickerTitle: string }[] = [
  { role: 'PRIMARY', label: 'Primary', pickerTitle: 'Primary Muscle' },
  { role: 'SECONDARY', label: 'Secondary', pickerTitle: 'Secondary Muscle' },
];

type ExerciseEditorProps = {
  /** null for a new exercise. */
  exercise: Exercise | null;
  muscleGroups: MuscleGroup[];
};

export default function ExerciseEditor({ exercise, muscleGroups }: ExerciseEditorProps) {
  const router = useRouter();
  const [name, setName] = useState(exercise?.name ?? '');
  const [description, setDescription] = useState(exercise?.description ?? '');
  const [trackingType, setTrackingType] = useState<TrackingType>(
    exercise?.trackingType ?? 'WEIGHT_REPS',
  );
  const [exerciseType, setExerciseType] = useState<ExerciseType>(
    exercise?.exerciseType ?? 'STRENGTH',
  );
  const [muscles, setMuscles] = useState<
    { id: number; name: string; role: MuscleRole }[]
  >(exercise?.muscles.map(({ id, name, role }) => ({ id, name, role })) ?? []);
  const [instructions, setInstructions] = useState(exercise?.instructions ?? '');
  const [nameError, setNameError] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [isSaving, setIsSaving] = useState(false);

  const tracking = trackingOptions.find((o) => o.value === trackingType)!;

  async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    if (isSaving) return;
    setNameError(null);
    setError(null);

    if (!name.trim()) {
      setNameError('Please enter a name.');
      return;
    }

    const payload = {
      name: name.trim(),
      exerciseType,
      trackingType,
      description: description.trim() || null,
      instructions: instructions.trim() || null,
      muscles: muscles.map(({ id, role }) => ({ muscleGroupId: id, role })),
    };

    setIsSaving(true);
    try {
      const saved = exercise
        ? await updateExercise(String(exercise.id), payload)
        : await createExercise(payload);
      toast.success(exercise ? 'Exercise saved.' : 'Exercise created.');
      router.replace(`/exercises/${saved.id}`);
      router.refresh();
    } catch (err) {
      setIsSaving(false);
      if (err instanceof ApiError && err.code === 'DATA_CONFLICT') {
        setNameError('You already have an exercise with this name.');
        return;
      }
      if (err instanceof ApiError && err.fieldErrors?.name) {
        setNameError(err.fieldErrors.name);
        return;
      }
      if (err instanceof ApiError && err.code === 'TRACKING_TYPE_LOCKED') {
        setError(
          'What you log per set can not change anymore, because this exercise is already in use.',
        );
        return;
      }
      setError(fallbackErrorMessage(err));
    }
  }

  return (
    <div className="mx-auto flex w-full max-w-app flex-1 flex-col pb-8 lg:max-w-[560px]">
      <form onSubmit={handleSubmit} noValidate>
        <header className="sticky top-(--app-top-inset) z-10 grid h-14 grid-cols-[1fr_minmax(0,2fr)_1fr] items-center border-b border-border-subtle bg-bg px-4">
          {exercise ? (
            <Link href={`/exercises/${exercise.id}`} replace className={cancelClass}>
              Cancel
            </Link>
          ) : (
            // A new exercise is only reached from a list, so going back avoids a second list entry.
            <button type="button" onClick={() => router.back()} className={cancelClass}>
              Cancel
            </button>
          )}
          <h1 className="truncate text-center text-[1.0625rem] font-semibold">
            {exercise ? exercise.name : 'New Exercise'}
          </h1>
          <Button
            type="submit"
            variant="primary"
            size="sm"
            className="justify-self-end"
            disabled={isSaving}
          >
            {isSaving ? 'Saving...' : 'Save'}
          </Button>
        </header>

        <div className="px-4 pt-4">
          {error && (
            <div
              role="alert"
              className="mb-4 flex items-start gap-2.5 rounded-field bg-danger-subtle p-3 text-meta"
            >
              <CircleAlert className="size-5 shrink-0 text-danger" aria-hidden="true" />
              <span>{error}</span>
            </div>
          )}

          <Label className="mb-1.5" htmlFor="exercise-name">
            Name
          </Label>
          <Input
            id="exercise-name"
            value={name}
            onChange={(e) => setName(e.target.value)}
            maxLength={128}
            autoComplete="off"
            aria-invalid={nameError ? true : undefined}
            aria-describedby={nameError ? 'exercise-name-error' : undefined}
          />
          {nameError && (
            <p
              id="exercise-name-error"
              className="mt-1.5 flex gap-1.5 text-meta text-danger"
            >
              <CircleAlert
                className="size-4 shrink-0 translate-y-0.5"
                aria-hidden="true"
              />
              {nameError}
            </p>
          )}

          <Label className="mt-4 mb-1.5" htmlFor="exercise-description">
            Description (optional)
          </Label>
          <textarea
            id="exercise-description"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            maxLength={1000}
            rows={2}
            className={textareaClass}
          />

          <h2 id="logging-title" className="mt-6 text-title">
            Logging
          </h2>
          <p className="mt-0.5 mb-3 text-meta text-text-secondary">
            What you enter for every set.
          </p>
          <span className="relative block">
            <select
              aria-labelledby="logging-title"
              aria-describedby="logging-hint"
              value={trackingType}
              onChange={(e) => {
                const next = trackingOptions.find((o) => o.value === e.target.value)!;
                setTrackingType(next.value);
                if (!next.exerciseTypes.includes(exerciseType)) {
                  setExerciseType(next.exerciseTypes[0]);
                }
              }}
              className="h-12 w-full min-w-0 cursor-pointer appearance-none rounded-field border border-border-strong bg-surface-raised pr-10 pl-3 text-field text-text-primary"
            >
              {trackingOptions.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </select>
            <ChevronDown
              className="pointer-events-none absolute top-1/2 right-3 size-5 -translate-y-1/2 text-text-secondary"
              aria-hidden="true"
            />
          </span>
          <p id="logging-hint" className="mt-1.5 text-meta text-text-tertiary">
            {tracking.hint}
          </p>

          {tracking.exerciseTypes.length > 1 && (
            <fieldset className="mt-4">
              <legend className="mb-1.5 text-meta font-semibold text-text-secondary">
                Type
              </legend>
              <div className="inline-grid grid-flow-col gap-1 rounded-card bg-surface p-1">
                {tracking.exerciseTypes.map((type) => (
                  <label
                    key={type}
                    className="grid min-h-9 cursor-pointer place-items-center rounded-field px-3.5 text-meta font-semibold text-text-tertiary hover:text-text-secondary has-checked:bg-surface-raised has-checked:text-text-primary has-focus-visible:outline-2 has-focus-visible:outline-focus"
                  >
                    <input
                      type="radio"
                      name="exercise-type"
                      value={type}
                      checked={exerciseType === type}
                      onChange={() => setExerciseType(type)}
                      className="sr-only"
                    />
                    {typeLabels[type]}
                  </label>
                ))}
              </div>
            </fieldset>
          )}

          <h2 className="mt-6 text-title">Muscles</h2>
          <p className="mt-0.5 text-meta text-text-secondary">
            Optional. The muscle filter finds the exercise by them.
          </p>
          {muscleRows.map((row) => (
            <fieldset key={row.role} className="mt-3">
              <legend className="mb-1.5 text-meta font-semibold text-text-secondary">
                {row.label}
              </legend>
              <ul className="flex flex-wrap gap-2">
                {muscles
                  .filter((muscle) => muscle.role === row.role)
                  .map((muscle) => (
                    <li
                      key={muscle.id}
                      className="inline-flex h-8.5 items-center rounded-full border border-border-strong pl-3 text-meta font-semibold"
                    >
                      {muscle.name}
                      <button
                        type="button"
                        aria-label={`Remove ${muscle.name}`}
                        onClick={() =>
                          setMuscles(muscles.filter((m) => m.id !== muscle.id))
                        }
                        className="relative inline-flex h-full items-center rounded-r-full pr-2.5 pl-1.5 text-text-secondary before:absolute before:inset-x-0 before:-inset-y-[5px] before:content-['']"
                      >
                        <X className="size-4" aria-hidden="true" />
                      </button>
                    </li>
                  ))}
                {muscles.length < MAX_MUSCLES && (
                  <li>
                    <MuscleGroupPicker
                      muscleGroups={muscleGroups}
                      title={row.pickerTitle}
                      pickedIds={muscles.map((muscle) => muscle.id)}
                      onPick={(muscle) =>
                        setMuscles([...muscles, { ...muscle, role: row.role }])
                      }
                    />
                  </li>
                )}
              </ul>
            </fieldset>
          ))}

          <Label className="mt-6 mb-1.5" htmlFor="exercise-instructions">
            Instructions (optional)
          </Label>
          <textarea
            id="exercise-instructions"
            value={instructions}
            onChange={(e) => setInstructions(e.target.value)}
            maxLength={4000}
            rows={4}
            placeholder="How to perform it, step by step"
            className={textareaClass}
          />
        </div>
      </form>

      {exercise && (
        <div className="mx-4 mt-8 border-t border-border-subtle pt-2">
          <DeleteExerciseDialog exerciseId={exercise.id} />
        </div>
      )}
    </div>
  );
}
