'use client';

import { UserRound } from 'lucide-react';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { TABS } from '@/shared/layout/tabs';

import { cn } from '@/shared/lib/utils';

type SideBarProps = {
  username: string | null;
};

export default function SideBar({ username }: SideBarProps) {
  const pathname = usePathname();
  const isAccountActive = pathname.startsWith('/me');

  return (
    <nav
      aria-label="Main"
      className="sticky top-0 hidden h-dvh w-62 shrink-0 flex-col self-start border-r border-border-subtle px-3 pt-5 pb-4 lg:flex"
    >
      <p className="px-3 pt-1.5 pb-6 text-title">
        <Link href="/">Fitness App</Link>
      </p>
      <ul className="flex flex-col gap-0.5">
        {TABS.map(({ label, href, icon: Icon, match }) => {
          const isActive = match.some((prefix) => pathname.startsWith(prefix));
          return (
            <li key={href}>
              <Link
                href={href}
                aria-current={isActive ? 'page' : undefined}
                className={cn(
                  'flex min-h-tap items-center gap-3 rounded-field px-3 font-semibold',
                  isActive
                    ? 'bg-surface-raised text-text-primary'
                    : 'text-text-secondary hover:bg-surface hover:text-text-primary',
                )}
              >
                <Icon className="size-5 shrink-0" aria-hidden="true" />
                {label}
              </Link>
            </li>
          );
        })}
      </ul>
      <Link
        href="/me"
        aria-current={isAccountActive ? 'page' : undefined}
        className={cn(
          'mt-auto flex items-center gap-3 rounded-field px-3 py-2',
          isAccountActive ? 'bg-surface-raised' : 'hover:bg-surface',
        )}
      >
        <span
          aria-hidden="true"
          className="grid size-9 shrink-0 place-items-center rounded-full bg-surface-raised font-semibold"
        >
          {username ? username[0].toUpperCase() : <UserRound className="size-5" />}
        </span>
        <span className="min-w-0">
          {username && <span className="block truncate font-semibold">{username}</span>}
          <span className="block text-meta text-text-tertiary">Account</span>
        </span>
      </Link>
    </nav>
  );
}
