'use client';

import { Trash2 } from 'lucide-react';
import { useTranslations } from 'next-intl';
import { useEffect, useRef, useState } from 'react';

import { useErrorMessage } from '@/shared/api/errors/use-error-message';
import { Button } from '@/shared/ui/button';
import { Sheet } from '@/shared/ui/sheet';
import ElapsedClock from './ElapsedClock';

type FinishSheetProps = {
  startedAt: string;
  /** Sets that are saved or still saving. */
  loggedCount: number;
  failedCount: number;
  onFinish: () => Promise<void>;
  onDiscard: () => Promise<void>;
  onClose: () => void;
};

//finish or discard the workout; the caller mounts it while open
export default function FinishSheet({
  startedAt,
  loggedCount,
  failedCount,
  onFinish,
  onDiscard,
  onClose,
}: FinishSheetProps) {
  const dialogRef = useRef<HTMLDialogElement>(null);
  const t = useTranslations('workouts.finish');
  const tCommon = useTranslations('common');
  const errorMessage = useErrorMessage();
  const [confirmDiscard, setConfirmDiscard] = useState(false);
  const [pending, setPending] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    dialogRef.current?.showModal();
  }, []);

  async function run(action: () => Promise<void>) {
    setPending(true);
    setError(null);
    try {
      await action();
    } catch (err) {
      setPending(false);
      setError(errorMessage(err));
    }
  }

  return (
    <Sheet
      ref={dialogRef}
      onClose={onClose}
      onCancel={(e) => {
        if (pending) e.preventDefault();
      }}
      aria-labelledby="finish-sheet-title"
    >
      {confirmDiscard ? (
        <>
          <h2 id="finish-sheet-title" className="text-title">
            {t('discardTitle')}
          </h2>
          <p className="mt-1 text-text-secondary">
            {loggedCount > 0
              ? t('discardWithSets', { count: loggedCount })
              : t('discardEmpty')}
          </p>
          {error && (
            <p role="alert" className="mt-3 text-meta text-danger">
              {error}
            </p>
          )}
          <div className="mt-5 flex flex-col gap-2">
            <Button
              type="button"
              variant="danger"
              size="lg"
              disabled={pending}
              onClick={() => run(onDiscard)}
            >
              {t('discard')}
            </Button>
            <Button
              type="button"
              size="lg"
              disabled={pending}
              onClick={() => {
                setConfirmDiscard(false);
                setError(null);
              }}
            >
              {tCommon('cancel')}
            </Button>
          </div>
        </>
      ) : (
        <>
          <h2 id="finish-sheet-title" className="text-title">
            {loggedCount > 0 ? t('title') : t('nothingTitle')}
          </h2>
          <p className="mt-1 text-text-secondary tabular-nums">
            {loggedCount > 0 ? (
              <>
                <ElapsedClock startedAt={startedAt} />,{' '}
                {t('logged', { count: loggedCount })}
              </>
            ) : (
              t('nothingText')
            )}
          </p>
          {failedCount > 0 && (
            <p className="mt-3 text-meta text-danger">
              {t('failed', { count: failedCount })}
            </p>
          )}
          {error && (
            <p role="alert" className="mt-3 text-meta text-danger">
              {error}
            </p>
          )}
          <div className="mt-5 flex flex-col gap-2">
            {loggedCount > 0 && (
              <Button
                type="button"
                variant="primary"
                size="lg"
                disabled={pending}
                onClick={() => run(onFinish)}
              >
                {pending ? t('submitting') : t('submit')}
              </Button>
            )}
            <Button
              type="button"
              size="lg"
              disabled={pending}
              onClick={() => dialogRef.current?.close()}
            >
              {t('keep')}
            </Button>
          </div>
          <div className="mt-4 border-t border-field-on-overlay pt-2">
            <Button
              type="button"
              variant="text"
              disabled={pending}
              className="w-full text-danger hover:bg-field-on-overlay active:bg-field-on-overlay"
              onClick={() => setConfirmDiscard(true)}
            >
              <Trash2 aria-hidden="true" />
              {t('discard')}
            </Button>
          </div>
        </>
      )}
    </Sheet>
  );
}
