'use client';

import { useRef, useState } from 'react';
import { useRouter } from 'next/navigation';
import { CircleAlert, Trash2 } from 'lucide-react';
import { toast } from 'sonner';

import { fallbackErrorMessage } from '@/shared/api/errors/fallback-error-message';
import { Button } from '@/shared/ui/button';
import { Sheet } from '@/shared/ui/sheet';
import { deleteRoutine } from '../client/delete-routine.client';

type DeleteRoutineDialogProps = {
  routineId: number;
};

export default function DeleteRoutineDialog({ routineId }: DeleteRoutineDialogProps) {
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
      await deleteRoutine(String(routineId));
      close();
      toast.success('Routine deleted.');
      router.replace('/routines');
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
        Delete routine
      </Button>

      <Sheet
        ref={dialogRef}
        onClose={() => setError(null)}
        onCancel={handleCancel}
        aria-labelledby="delete-routine-title"
      >
        <h2 id="delete-routine-title" className="text-title">
          Delete this routine?
        </h2>
        <p className="mt-1 mb-4 text-text-secondary">
          Workouts you logged with this routine stay complete.
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
            {isDeleting ? 'Deleting...' : 'Delete routine'}
          </Button>
          <Button type="button" className="w-full" onClick={close} disabled={isDeleting}>
            Cancel
          </Button>
        </div>
      </Sheet>
    </>
  );
}
