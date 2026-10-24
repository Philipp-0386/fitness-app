'use client';

import { useRef, useState } from 'react';
import { useRouter } from 'next/navigation';
import { CircleAlert, Trash2 } from 'lucide-react';
import { toast } from 'sonner';

import { fallbackErrorMessage } from '@/shared/api/errors/fallback-error-message';
import { Button } from '@/shared/ui/button';
import { Sheet } from '@/shared/ui/sheet';
import { deleteExercise } from '../client/delete-exercise.client';

type DeleteExerciseDialogProps = {
  exerciseId: number;
};

export default function DeleteExerciseDialog({ exerciseId }: DeleteExerciseDialogProps) {
  const dialogRef = useRef<HTMLDialogElement>(null);
  const [error, setError] = useState<string | null>(null);
  const [isDeleting, setIsDeleting] = useState(false);
  const router = useRouter();

  function close() {
    dialogRef.current?.close();
  }

  function handleCancel(e: React.SyntheticEvent<HTMLDialogElement>) {
    if (isDeleting) e.preventDefault();
  }

  async function handleDelete() {
    if (isDeleting) return;
    setError(null);
    setIsDeleting(true);
    try {
      await deleteExercise(String(exerciseId));
      close();
      toast.success('Exercise deleted.');
      router.replace('/exercises');
      router.refresh();
    } catch (err) {
      setIsDeleting(false);
      setError(fallbackErrorMessage(err));
    }
  }

  return (
    <>
      <Button
        type="button"
        variant="text"
        className="w-full text-danger"
        onClick={() => dialogRef.current?.showModal()}
      >
        <Trash2 aria-hidden="true" />
        Delete exercise
      </Button>

      <Sheet
        ref={dialogRef}
        onClose={() => setError(null)}
        onCancel={handleCancel}
        aria-labelledby="delete-exercise-title"
      >
        <h2 id="delete-exercise-title" className="text-title">
          Delete this exercise?
        </h2>
        <p className="mt-1 mb-4 text-text-secondary">
          It is removed from your routines. Workouts you logged with it stay complete.
        </p>
        {error && (
          <div
            role="alert"
            className="mb-2 flex items-start gap-2.5 rounded-field bg-danger-subtle p-3 text-meta"
          >
            <CircleAlert className="size-5 shrink-0 text-danger" aria-hidden="true" />
            <span>{error}</span>
          </div>
        )}
        <div className="mt-2 flex flex-col gap-2">
          <Button
            type="button"
            variant="danger"
            size="lg"
            onClick={handleDelete}
            disabled={isDeleting}
          >
            {isDeleting ? 'Deleting...' : 'Delete exercise'}
          </Button>
          <Button type="button" className="w-full" onClick={close} disabled={isDeleting}>
            Cancel
          </Button>
        </div>
      </Sheet>
    </>
  );
}
