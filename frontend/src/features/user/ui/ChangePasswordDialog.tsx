'use client';

import { useRef, useState } from 'react';
import { toast } from 'sonner';

import { ApiError } from '@/shared/api/errors/api-error';
import { updatePassword } from '../client/update-password.client';

export default function ChangePasswordDialog() {
  const dialogRef = useRef<HTMLDialogElement>(null);
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [repeatPassword, setRepeatPassword] = useState('');
  const [isSaving, setIsSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  function open() {
    dialogRef.current?.showModal();
  }

  function close() {
    dialogRef.current?.close();
  }

  function handleClose() {
    setCurrentPassword('');
    setNewPassword('');
    setRepeatPassword('');
    setError(null);
  }

  function handleCancel(e: React.SyntheticEvent<HTMLDialogElement>) {
    if (isSaving) {
      e.preventDefault();
    }
  }

  async function handleSubmit(e: React.SubmitEvent<HTMLFormElement>) {
    e.preventDefault();
    if (isSaving) return;
    setError(null);

    if (!currentPassword || !newPassword || !repeatPassword) {
      setError('Please fill in all fields!');
      return;
    }

    if (newPassword === currentPassword) {
      setError('The new password must be different from the current one.');
      return;
    }

    if (newPassword !== repeatPassword) {
      setError('The new passwords do not match.');
      return;
    }

    setIsSaving(true);
    try {
      await updatePassword({ currentPassword, newPassword });
      close();
      toast.success('Your password has been changed.');
    } catch (err) {
      if (err instanceof ApiError) {
        setError(err.message);
        return;
      }
      setError('An unexpected error occurred. Please try again later.');
    } finally {
      setIsSaving(false);
    }
  }

  return (
    <>
      <button type="button" onClick={open}>
        Change password
      </button>

      <dialog ref={dialogRef} onClose={handleClose} onCancel={handleCancel}>
        <form onSubmit={handleSubmit} noValidate>
          <h3>Change your password</h3>
          <label>
            Current password:
            <br />
            <input
              type="password"
              value={currentPassword}
              onChange={(e) => setCurrentPassword(e.target.value)}
              autoComplete="current-password"
            />
          </label>
          <br />
          <label>
            New password:
            <br />
            <input
              type="password"
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              autoComplete="new-password"
            />
          </label>
          <br />
          <label>
            Repeat new password:
            <br />
            <input
              type="password"
              value={repeatPassword}
              onChange={(e) => setRepeatPassword(e.target.value)}
              autoComplete="new-password"
            />
          </label>
          {error && <p>{error}</p>}
          <p>
            <button type="button" disabled={isSaving} onClick={close}>
              Cancel
            </button>{' '}
            <button type="submit" disabled={isSaving}>
              {isSaving ? 'Changing password...' : 'Change password'}
            </button>
          </p>
        </form>
      </dialog>
    </>
  );
}
