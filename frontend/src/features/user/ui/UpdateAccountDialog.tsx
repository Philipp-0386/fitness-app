'use client';

import { useRef, useState } from 'react';
import { updateMe } from '../client/update-user.client';
import { toast } from 'sonner';
import { useRouter } from 'next/navigation';
import { ChevronRight, CircleAlert } from 'lucide-react';
import { ApiError } from '@/shared/api/errors/api-error';
import { fallbackErrorMessage } from '@/shared/api/errors/fallback-error-message';
import { Button } from '@/shared/ui/button';
import { Input } from '@/shared/ui/input';
import { Label } from '@/shared/ui/label';
import { listRowClass } from '@/shared/ui/list';
import { Sheet } from '@/shared/ui/sheet';

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
      <button type="button" onClick={open} className={listRowClass}>
        <span className="min-w-0 flex-1 font-semibold">Edit account</span>
        <ChevronRight className="size-5 shrink-0 text-text-tertiary" aria-hidden="true" />
      </button>

      <Sheet
        ref={dialogRef}
        onClose={handleClose}
        onCancel={handleCancel}
        aria-labelledby="update-account-title"
      >
        <form onSubmit={handleSubmit} noValidate>
          <h2 id="update-account-title" className="mb-4 text-title">
            Edit account
          </h2>
          <div className="mb-4">
            <Label className="mb-1.5" htmlFor="update-username">
              Username
            </Label>
            <Input
              type="text"
              id="update-username"
              value={newUsername}
              onChange={(e) => setNewUsername(e.target.value)}
              autoComplete="username"
            />
          </div>
          <div className="mb-4">
            <Label className="mb-1.5" htmlFor="update-email">
              Email
            </Label>
            <Input
              type="email"
              id="update-email"
              value={newEmail}
              onChange={(e) => setNewEmail(e.target.value)}
              autoComplete="email"
            />
          </div>
          <div className="mb-4">
            <Label className="mb-1.5" htmlFor="update-password">
              Confirm with your password
            </Label>
            <Input
              type="password"
              id="update-password"
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
            <Button type="submit" variant="primary" size="lg" disabled={isSaving}>
              {isSaving ? 'Saving changes...' : 'Save changes'}
            </Button>
            <Button type="button" className="w-full" disabled={isSaving} onClick={close}>
              Cancel
            </Button>
          </div>
        </form>
      </Sheet>
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
      case 'VALIDATION_FAILED':
        if (error.fieldErrors) return Object.values(error.fieldErrors).join(' ');
        break;
    }
  }
  return fallbackErrorMessage(error);
}
