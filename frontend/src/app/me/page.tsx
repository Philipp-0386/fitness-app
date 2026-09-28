import Link from 'next/link';

import {
  ChangePasswordDialog,
  DeleteAccountDialog,
  UpdateAccountDialog,
  fetchMe,
} from '@/features/user';
import { requireSession } from '@/shared/auth/session';

export default async function MePage() {
  await requireSession();

  const user = await fetchMe();

  return (
    <main>
      <h1>Your account</h1>
      <dl>
        <dt>Username</dt>
        <dd>{user.username}</dd>
        <dt>Email</dt>
        <dd>{user.email}</dd>
        <dt>Member since</dt>
        <dd>{new Date(user.createdAt).toLocaleDateString('en-GB')}</dd>
      </dl>

      <h2>Update Account</h2>
      <p>Allows you to update information regarding your account.</p>
      <UpdateAccountDialog username={user.username} email={user.email} />

      <h2>Change password</h2>
      <p>Other sessions stay logged in until they expire.</p>
      <ChangePasswordDialog />

      <h2>Delete account</h2>
      <p>
        Permanently deletes your account and everything you created: own exercises, plans
        and logged sessions.
      </p>
      <DeleteAccountDialog />
      <p>
        <Link href="/dashboard">Back to dashboard</Link>
      </p>
    </main>
  );
}
