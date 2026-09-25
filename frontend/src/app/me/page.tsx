import Link from 'next/link';

import { DeleteAccountDialog, fetchMe } from '@/features/user';
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
