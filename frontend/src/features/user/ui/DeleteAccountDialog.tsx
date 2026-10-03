'use client';

import { useRef, useState } from 'react';
import { useRouter } from 'next/navigation';
import { toast } from 'sonner';

import { ApiError } from '@/shared/api/errors/api-error';
import { fallbackErrorMessage } from '@/shared/api/errors/fallback-error-message';
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
      <button type="button" onClick={open}>
        Delete account
      </button>

      <dialog ref={dialogRef} onClose={handleClose} onCancel={handleCancel}>
        <form onSubmit={handleSubmit} noValidate>
          <h3>Delete your account?</h3>
          <p>
            This permanently deletes your account and all your data. It cannot be undone.
          </p>
          <label>
            Confirm with your password:
            <br />
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              autoComplete="current-password"
            />
          </label>
          {error && <p>{error}</p>}
          <p>
            <button type="button" onClick={close} disabled={isDeleting}>
              Cancel
            </button>{' '}
            <button type="submit" disabled={isDeleting}>
              {isDeleting ? 'Deleting...' : 'Delete account'}
            </button>
          </p>
        </form>
      </dialog>
    </>
  );
}
