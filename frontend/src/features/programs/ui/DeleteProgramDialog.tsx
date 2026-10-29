'use client';

import { useRef, useState } from 'react';
import { useRouter } from 'next/navigation';
import { CircleAlert, Trash2 } from 'lucide-react';
import { toast } from 'sonner';

import { fallbackErrorMessage } from '@/shared/api/errors/fallback-error-message';
import { Button } from '@/shared/ui/button';
import { Sheet } from '@/shared/ui/sheet';
import { deleteProgram } from '../client/delete-program.client';

type DeleteProgramDialogProps = {
  programId: number;
  active: boolean;
};

export default function DeleteProgramDialog({
  programId,
  active,
}: DeleteProgramDialogProps) {
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
      await deleteProgram(String(programId));
      close();
      toast.success('Program deleted.');
      router.replace('/plans');
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
        Delete Program
      </Button>

      <Sheet
        ref={dialogRef}
        onClose={() => setError(null)}
        onCancel={handleCancel}
        aria-labelledby="delete-program-title"
      >
        <h2 id="delete-program-title" className="text-title">
          Delete this program?
        </h2>
        <p className="mt-1 mb-4 text-text-secondary">
          Your logged workouts stay complete.
          {active &&
            ' This is your active program, Today shows no program until you activate another one.'}
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
            {isDeleting ? 'Deleting...' : 'Delete Program'}
          </Button>
          <Button type="button" className="w-full" onClick={close} disabled={isDeleting}>
            Cancel
          </Button>
        </div>
      </Sheet>
    </>
  );
}
