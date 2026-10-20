'use client';

import { useRef, useState } from 'react';
import { ChevronRight, CircleAlert } from 'lucide-react';
import { toast } from 'sonner';

import { ApiError } from '@/shared/api/errors/api-error';
import { fallbackErrorMessage } from '@/shared/api/errors/fallback-error-message';
import { Button } from '@/shared/ui/button';
import { Input } from '@/shared/ui/input';
import { Label } from '@/shared/ui/label';
import { listRowClass } from '@/shared/ui/list';
import { Sheet } from '@/shared/ui/sheet';
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
      if (err instanceof ApiError && err.code === 'INVALID_PASSWORD') {
        setError('The current password is incorrect.');
        return;
      }
      if (
        err instanceof ApiError &&
        err.code === 'VALIDATION_FAILED' &&
        err.fieldErrors
      ) {
        setError(Object.values(err.fieldErrors).join(' '));
        return;
      }
      setError(fallbackErrorMessage(err));
    } finally {
      setIsSaving(false);
    }
  }

  return (
    <>
      <button type="button" onClick={open} className={listRowClass}>
        <span className="min-w-0 flex-1 font-semibold">Change password</span>
        <ChevronRight className="size-5 shrink-0 text-text-tertiary" aria-hidden="true" />
      </button>

      <Sheet
        ref={dialogRef}
        onClose={handleClose}
        onCancel={handleCancel}
        aria-labelledby="change-password-title"
      >
        <form onSubmit={handleSubmit} noValidate>
          <h2 id="change-password-title" className="text-title">
            Change password
          </h2>
          <p className="mt-1 mb-4 text-text-secondary">
            Other sessions stay logged in until they expire.
          </p>
          <div className="mb-4">
            <Label className="mb-1.5" htmlFor="change-current-password">
              Current password
            </Label>
            <Input
              type="password"
              id="change-current-password"
              value={currentPassword}
              onChange={(e) => setCurrentPassword(e.target.value)}
              autoComplete="current-password"
            />
          </div>
          <div className="mb-4">
            <Label className="mb-1.5" htmlFor="change-new-password">
              New password
            </Label>
            <Input
              type="password"
              id="change-new-password"
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              autoComplete="new-password"
            />
          </div>
          <div className="mb-4">
            <Label className="mb-1.5" htmlFor="change-repeat-password">
              Repeat new password
            </Label>
            <Input
              type="password"
              id="change-repeat-password"
              value={repeatPassword}
              onChange={(e) => setRepeatPassword(e.target.value)}
              autoComplete="new-password"
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
              {isSaving ? 'Changing password...' : 'Change password'}
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
