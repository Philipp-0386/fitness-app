'use client';

import { useRef, useState } from 'react';
import { useRouter } from 'next/navigation';
import { CircleAlert, Trash2 } from 'lucide-react';
import { useTranslations } from 'next-intl';
import { toast } from 'sonner';

import { useErrorMessage } from '@/shared/api/errors/use-error-message';
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
  const t = useTranslations('routines.delete');
  const tCommon = useTranslations('common');
  const errorMessage = useErrorMessage();

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
      toast.success(t('deleted'));
      router.replace('/plans');
      router.refresh();
    } catch (err) {
      setIsDeleting(false);
      setError(errorMessage(err));
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
        {t('button')}
      </Button>

      <Sheet
        ref={dialogRef}
        onClose={() => setError(null)}
        onCancel={handleCancel}
        aria-labelledby="delete-routine-title"
      >
        <h2 id="delete-routine-title" className="text-title">
          {t('title')}
        </h2>
        <p className="mt-1 mb-4 text-text-secondary">{t('text')}</p>
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
            {isDeleting ? tCommon('deleting') : t('button')}
          </Button>
          <Button type="button" className="w-full" onClick={close} disabled={isDeleting}>
            {tCommon('cancel')}
          </Button>
        </div>
      </Sheet>
    </>
  );
}
