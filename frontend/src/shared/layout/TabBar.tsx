'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { TABS } from '@/shared/layout/tabs';

import { cn } from '@/shared/lib/utils';

//mobile tab bar for the app, only visible on mobile devices/thin viewport
export default function TabBar() {
  const pathname = usePathname();

  return (
    <>
      <div
        aria-hidden="true"
        className="h-[calc(3.25rem+0.5rem+1px+env(safe-area-inset-bottom))] lg:hidden"
      />
      <nav
        aria-label="Main"
        className="fixed inset-x-0 bottom-0 z-10 border-t border-border-subtle bg-bg pb-[env(safe-area-inset-bottom)] lg:hidden"
      >
        <div className="mx-auto grid max-w-app grid-cols-4 p-1">
          {TABS.map(({ label, href, icon: Icon, match }) => {
            const isActive = match.some((prefix) => pathname.startsWith(prefix));
            return (
              <Link
                key={href}
                href={href}
                aria-current={isActive ? 'page' : undefined}
                className={cn(
                  'flex min-h-13 flex-col items-center justify-center gap-0.5 rounded-field text-caption font-semibold active:bg-surface',
                  isActive ? 'text-text-primary' : 'text-text-tertiary',
                )}
              >
                <Icon className="size-6" aria-hidden="true" />
                {label}
              </Link>
            );
          })}
        </div>
      </nav>
    </>
  );
}
