'use client';

import { Trash2 } from 'lucide-react';
import { useEffect, useRef, useState } from 'react';

import { fallbackErrorMessage } from '@/shared/api/errors/fallback-error-message';
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
      setError(fallbackErrorMessage(err));
    }
  }

  const setsText = `${loggedCount} ${loggedCount === 1 ? 'set' : 'sets'} logged`;

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
            Discard this workout?
          </h2>
          <p className="mt-1 text-text-secondary">
            {loggedCount > 0
              ? `The workout and its ${setsText} are deleted. This cannot be undone.`
              : 'The workout is deleted. This cannot be undone.'}
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
              Discard Workout
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
              Cancel
            </Button>
          </div>
        </>
      ) : (
        <>
          <h2 id="finish-sheet-title" className="text-title">
            {loggedCount > 0 ? 'Finish workout?' : 'Nothing logged yet'}
          </h2>
          <p className="mt-1 text-text-secondary tabular-nums">
            {loggedCount > 0 ? (
              <>
                <ElapsedClock startedAt={startedAt} />, {setsText}
              </>
            ) : (
              'Log a set to finish the workout, or discard it.'
            )}
          </p>
          {failedCount > 0 && (
            <p className="mt-3 text-meta text-danger">
              {failedCount === 1 ? '1 set was' : `${failedCount} sets were`} not saved.
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
                {pending ? 'Finishing...' : 'Finish Workout'}
              </Button>
            )}
            <Button
              type="button"
              size="lg"
              disabled={pending}
              onClick={() => dialogRef.current?.close()}
            >
              Keep Training
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
              Discard Workout
            </Button>
          </div>
        </>
      )}
    </Sheet>
  );
}
