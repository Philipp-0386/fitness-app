'use client';

import { useRef, useState } from 'react';
import { updateMe } from '../client/update-user.client';
import { toast } from 'sonner';
import { useRouter } from 'next/navigation';
import { ApiError } from '@/shared/api/errors/api-error';
import { fallbackErrorMessage } from '@/shared/api/errors/fallback-error-message';

type Props = {
  username: string;
  email: string;
};

export default function UpdateAccountDialog({ username, email }: Props) {
  const dialogRef = useRef<HTMLDialogElement>(null);
  const [newUsername, setNewUsername] = useState(username);
  const [newEmail, setNewEmail] = useState(email);
  const [password, setPassword] = useState('');
  const [isSaving, setIsSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const router = useRouter();

  function open() {
    setNewEmail(email);
    setNewUsername(username);
    dialogRef.current?.showModal();
  }

  function close() {
    dialogRef.current?.close();
  }

  function handleClose() {
    setPassword('');
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

    if (!newUsername || !newEmail || !password) {
      setError('Please fill in all fields!');
      return;
    }

    setIsSaving(true);
    try {
      const user = await updateMe({
        username: newUsername,
        email: newEmail,
        currentPassword: password,
      });
      close();
      toast.success(`Account updated: ${user.username}`);
      router.refresh();
    } catch (err) {
      setError(evaluateError(err));
    } finally {
      setIsSaving(false);
    }
  }
  return (
    <>
      <button type="button" onClick={open}>
        Update account information
      </button>

      <dialog ref={dialogRef} onClose={handleClose} onCancel={handleCancel}>
        <form onSubmit={handleSubmit} noValidate>
          <h3>Change username or email</h3>
          <label>
            Username:
            <br />
            <input
              type="text"
              value={newUsername}
              onChange={(e) => setNewUsername(e.target.value)}
              autoComplete="username"
            />
          </label>
          <br />
          <label>
            Email:
            <br />
            <input
              type="email"
              value={newEmail}
              onChange={(e) => setNewEmail(e.target.value)}
              autoComplete="email"
            />
          </label>
          <br />
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
            <button type="button" disabled={isSaving} onClick={close}>
              Cancel
            </button>{' '}
            <button type="submit" disabled={isSaving}>
              {isSaving ? 'Saving changes...' : 'Save changes'}
            </button>
          </p>
        </form>
      </dialog>
    </>
  );
}

function evaluateError(error: unknown): string {
  if (error instanceof ApiError) {
    switch (error.code) {
      case 'INVALID_PASSWORD':
        return 'The password is incorrect.';
      case 'USERNAME_ALREADY_TAKEN':
        return 'This username is already taken. Please choose another one.';
      case 'EMAIL_ALREADY_EXISTS':
        return 'An account with this email already exists. Please use another one.';
    }
  }
  return fallbackErrorMessage(error);
}
