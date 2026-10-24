'use client';

import { CircleAlert } from 'lucide-react';
import { useRouter } from 'next/navigation';
import { useRef, useState } from 'react';
import { toast } from 'sonner';

import { ApiError } from '@/shared/api/errors/api-error';
import { fallbackErrorMessage } from '@/shared/api/errors/fallback-error-message';
import { Button } from '@/shared/ui/button';
import { Input } from '@/shared/ui/input';
import { Label } from '@/shared/ui/label';
import { Sheet } from '@/shared/ui/sheet';
import { createExercise } from '../client/create-exercise.client';
import { updateExercise } from '../client/update-exercise.client';
import { trackingOptions } from '../lib/tracking-options';
import { typeLabels } from '../lib/type-labels';
import { Exercise, ExerciseType, TrackingType } from '../types/api.types';

const textareaClass =
  'w-full min-w-0 rounded-field border border-border-on-overlay bg-field-on-overlay px-3 py-2.5 text-field text-text-primary placeholder:text-text-secondary';

type ExerciseFormDialogProps = {
  /** Edits this exercise, without one the dialog creates a new exercise. */
  exercise?: Exercise;
};

export default function ExerciseFormDialog({ exercise }: ExerciseFormDialogProps) {
  const dialogRef = useRef<HTMLDialogElement>(null);
  const router = useRouter();
  const [trackingType, setTrackingType] = useState<TrackingType>(
    exercise?.trackingType ?? 'WEIGHT_REPS',
  );
  const [exerciseType, setExerciseType] = useState<ExerciseType>(
    exercise?.exerciseType ?? 'STRENGTH',
  );
  const [name, setName] = useState(exercise?.name ?? '');
  const [description, setDescription] = useState(exercise?.description ?? '');
  const [instructions, setInstructions] = useState(exercise?.instructions ?? '');
  const [nameError, setNameError] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [isSaving, setIsSaving] = useState(false);

  const allowedTypes = trackingOptions.find(
    (o) => o.value === trackingType,
  )!.exerciseTypes;

  function handleCancel(e: React.SyntheticEvent<HTMLDialogElement>) {
    if (isSaving) e.preventDefault();
  }

  async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    if (isSaving) return;
    setNameError(null);
    setError(null);

    if (!name.trim()) {
      setNameError('Please enter a name.');
      return;
    }

    setIsSaving(true);
    try {
      const payload = {
        name: name.trim(),
        exerciseType,
        trackingType,
        description: description.trim() || null,
        instructions: instructions.trim() || null,
      };
      if (exercise) {
        await updateExercise(String(exercise.id), payload);
      } else {
        await createExercise(payload);
      }
      setIsSaving(false);
      dialogRef.current?.close();
      if (!exercise) {
        setTrackingType('WEIGHT_REPS');
        setExerciseType('STRENGTH');
        setName('');
        setDescription('');
        setInstructions('');
      }
      toast.success(exercise ? 'Exercise saved.' : 'Exercise created.');
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
    <>
      <Button type="button" size="sm" onClick={() => dialogRef.current?.showModal()}>
        {exercise ? 'Edit' : 'New Exercise'}
      </Button>

      <Sheet
        ref={dialogRef}
        onClose={() => {
          setNameError(null);
          setError(null);
        }}
        onCancel={handleCancel}
        aria-labelledby="exercise-form-title"
      >
        <form onSubmit={handleSubmit} noValidate>
          <div className="flex items-start justify-between gap-3">
            <h2 id="exercise-form-title" className="text-title">
              {exercise ? 'Edit Exercise' : 'New Exercise'}
            </h2>
            <Button
              type="button"
              variant="text"
              size="sm"
              onClick={() => dialogRef.current?.close()}
              disabled={isSaving}
            >
              Cancel
            </Button>
          </div>
          <p className="mt-1 mb-4 text-text-secondary">Only visible to you.</p>

          <fieldset>
            <legend className="mb-1.5 text-meta font-semibold text-text-secondary">
              What do you log per set?
            </legend>
            <div className="divide-y divide-field-on-overlay overflow-hidden rounded-card border border-field-on-overlay">
              {trackingOptions.map((option) => (
                <label
                  key={option.value}
                  className="flex min-h-14 cursor-pointer items-center gap-3 px-4 py-2.5 has-checked:bg-field-on-overlay"
                >
                  <input
                    type="radio"
                    name="tracking-type"
                    value={option.value}
                    checked={trackingType === option.value}
                    onChange={() => {
                      setTrackingType(option.value);
                      if (!option.exerciseTypes.includes(exerciseType)) {
                        setExerciseType(option.exerciseTypes[0]);
                      }
                    }}
                    className="size-5 shrink-0 accent-action"
                  />
                  <span className="min-w-0 flex-1">
                    <span className="block font-semibold">{option.label}</span>
                    <span className="block text-meta text-text-secondary">
                      {option.hint}
                    </span>
                  </span>
                </label>
              ))}
            </div>
          </fieldset>

          {allowedTypes.length > 1 && (
            <fieldset className="mt-4">
              <legend className="mb-1.5 text-meta font-semibold text-text-secondary">
                Type
              </legend>
              <div className="inline-grid grid-flow-col gap-1 rounded-card bg-surface p-1">
                {allowedTypes.map((type) => (
                  <label
                    key={type}
                    className="grid min-h-9 cursor-pointer place-items-center rounded-field px-3.5 text-meta font-semibold text-text-tertiary hover:text-text-secondary has-checked:bg-field-on-overlay has-checked:text-text-primary has-focus-visible:outline-2 has-focus-visible:outline-focus"
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

          <Label className="mt-4 mb-1.5" htmlFor="exercise-name">
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

          <Label className="mt-4 mb-1.5" htmlFor="exercise-instructions">
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

          {error && (
            <div
              role="alert"
              className="mt-4 flex items-start gap-2.5 rounded-field bg-danger-subtle p-3 text-meta"
            >
              <CircleAlert className="size-5 shrink-0 text-danger" aria-hidden="true" />
              <span>{error}</span>
            </div>
          )}

          <div className="mt-5 flex flex-col gap-2">
            <Button type="submit" variant="primary" size="lg" disabled={isSaving}>
              {exercise
                ? isSaving
                  ? 'Saving...'
                  : 'Save'
                : isSaving
                  ? 'Creating...'
                  : 'Create'}
            </Button>
          </div>
        </form>
      </Sheet>
    </>
  );
}
