import Link from 'next/link';
import { ChevronRight, ExternalLink } from 'lucide-react';

import { LogoutButton } from '@/features/home';
import {
  ChangePasswordDialog,
  DeleteAccountDialog,
  UpdateAccountDialog,
  fetchMe,
} from '@/features/user';
import { requireSession } from '@/shared/auth/session';
import { cn } from '@/shared/lib/utils';
import { GITHUB_URL } from '@/shared/site';
import { listClass, listRowClass } from '@/shared/ui/list';

export default async function MePage() {
  await requireSession();

  const user = await fetchMe();
  const memberSince = new Date(user.createdAt).toLocaleDateString('en-GB', {
    month: 'short',
    year: 'numeric',
  });

  return (
    <main className="mx-auto flex w-full max-w-app flex-1 flex-col pb-8">
      <header className="flex min-h-tap items-center px-4 pt-4">
        <h1 className="text-display">Account</h1>
      </header>

      <section aria-label="Profile" className="flex items-center gap-3.5 px-4 pt-3 pb-1">
        <span
          aria-hidden="true"
          className="grid size-14 shrink-0 place-items-center rounded-full bg-surface-raised text-title"
        >
          {user.username[0].toUpperCase()}
        </span>
        <div className="min-w-0">
          <p className="truncate text-[1.0625rem] font-semibold">{user.username}</p>
          <p className="truncate text-meta text-text-tertiary">{user.email}</p>
          <p className="text-meta text-text-tertiary">Member since {memberSince}</p>
        </div>
      </section>

      <ul className={cn(listClass, 'mx-4 mt-5')}>
        <li>
          <UpdateAccountDialog username={user.username} email={user.email} />
        </li>
        <li>
          <ChangePasswordDialog />
        </li>
      </ul>

      <nav aria-label="Site" className="mx-4 mt-5 lg:hidden">
        <ul className={listClass}>
          <li>
            <Link href="/about" className={listRowClass}>
              <span className="min-w-0 flex-1 font-semibold">About</span>
              <ChevronRight
                className="size-5 shrink-0 text-text-tertiary"
                aria-hidden="true"
              />
            </Link>
          </li>
          <li>
            <Link href="/datenschutz" className={listRowClass}>
              <span className="min-w-0 flex-1 font-semibold">Privacy policy</span>
              <span className="text-meta text-text-secondary">German</span>
              <ChevronRight
                className="size-5 shrink-0 text-text-tertiary"
                aria-hidden="true"
              />
            </Link>
          </li>
          <li>
            <a
              href={GITHUB_URL}
              className={listRowClass}
              target="_blank"
              rel="noopener noreferrer"
            >
              <span className="min-w-0 flex-1 font-semibold">
                GitHub<span className="sr-only"> (opens in a new tab)</span>
              </span>
              <ExternalLink
                className="size-5 shrink-0 text-text-tertiary"
                aria-hidden="true"
              />
            </a>
          </li>
        </ul>
      </nav>

      <div className={cn(listClass, 'mx-4 mt-5')}>
        <LogoutButton />
      </div>

      <section aria-label="Danger zone" className="mx-4 mt-8">
        <ul className={listClass}>
          <li>
            <DeleteAccountDialog />
          </li>
        </ul>
        <p className="mx-1 mt-2 text-meta text-text-tertiary">
          Permanently deletes your account and everything you created: own exercises,
          plans and logged sessions.
        </p>
      </section>
    </main>
  );
}
