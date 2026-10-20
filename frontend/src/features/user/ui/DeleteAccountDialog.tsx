'use client';

import { useRef, useState } from 'react';
import { useRouter } from 'next/navigation';
import { CircleAlert } from 'lucide-react';
import { toast } from 'sonner';

import { ApiError } from '@/shared/api/errors/api-error';
import { fallbackErrorMessage } from '@/shared/api/errors/fallback-error-message';
import { Button } from '@/shared/ui/button';
import { Input } from '@/shared/ui/input';
import { Label } from '@/shared/ui/label';
import { listRowClass } from '@/shared/ui/list';
import { Sheet } from '@/shared/ui/sheet';
import { deleteMe } from '../client/delete-user.client';

export default function DeleteAccountDialog() {
  const dialogRef = useRef<HTMLDialogElement>(null);
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [isDeleting, setIsDeleting] = useState(false);
  const router = useRouter();

  function open() {
    dialogRef.current?.showModal();
  }

  function close() {
    dialogRef.current?.close();
  }

  // Runs for every way of closing: the cancel button, Escape and a successful deletion.
  function handleClose() {
    setPassword('');
    setError(null);
  }

  function handleCancel(e: React.SyntheticEvent<HTMLDialogElement>) {
    if (isDeleting) e.preventDefault();
  }

  async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    if (isDeleting) return;
    setError(null);

    if (!password) {
      setError('Please enter your password.');
      return;
    }

    setIsDeleting(true);
    try {
      await deleteMe({ password });
      close();
      toast.success('Your account has been deleted.');
      router.replace('/');
      router.refresh();
    } catch (err) {
      setIsDeleting(false);
      if (err instanceof ApiError && err.code === 'INVALID_PASSWORD') {
        setError('The password is incorrect.');
        return;
      }
      setError(fallbackErrorMessage(err));
    }
  }

  return (
    <>
      <button type="button" onClick={open} className={listRowClass}>
        <span className="min-w-0 flex-1 font-semibold text-danger">Delete account</span>
      </button>

      <Sheet
        ref={dialogRef}
        onClose={handleClose}
        onCancel={handleCancel}
        aria-labelledby="delete-account-title"
      >
        <form onSubmit={handleSubmit} noValidate>
          <h2 id="delete-account-title" className="text-title">
            Delete your account?
          </h2>
          <p className="mt-1 mb-4 text-text-secondary">
            This permanently deletes your account and all your data. It cannot be undone.
          </p>
          <div className="mb-4">
            <Label className="mb-1.5" htmlFor="delete-password">
              Confirm with your password
            </Label>
            <Input
              type="password"
              id="delete-password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              autoComplete="current-password"
            />
          </div>
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
            <Button type="submit" variant="danger" size="lg" disabled={isDeleting}>
              {isDeleting ? 'Deleting...' : 'Delete account'}
            </Button>
            <Button
              type="button"
              className="w-full"
              onClick={close}
              disabled={isDeleting}
            >
              Cancel
            </Button>
          </div>
        </form>
      </Sheet>
    </>
  );
}
