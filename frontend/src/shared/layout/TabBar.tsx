'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { useTranslations } from 'next-intl';
import { TABS } from '@/shared/layout/tabs';

import { cn } from '@/shared/lib/utils';

//mobile tab bar for the app, only visible on mobile devices/thin viewport
export default function TabBar() {
  const pathname = usePathname();
  const t = useTranslations('nav');

  return (
    <>
      <div
        aria-hidden="true"
        className="h-[calc(4rem+0.75rem+max(0.75rem,env(safe-area-inset-bottom)_-_0.75rem))] lg:hidden"
      />
      <nav
        aria-label={t('main')}
        className="fixed inset-x-3 bottom-[max(0.75rem,env(safe-area-inset-bottom)_-_0.75rem)] z-10 lg:hidden"
      >
        <div className="mx-auto grid max-w-app grid-cols-4 gap-1 rounded-full bg-surface-overlay p-1 shadow-[0_8px_24px_rgb(0_0_0/0.5)]">
          {TABS.map(({ labelKey, href, icon: Icon, match }) => {
            const isActive = match.some((prefix) => pathname.startsWith(prefix));
            return (
              <Link
                key={href}
                href={href}
                aria-current={isActive ? 'page' : undefined}
                className={cn(
                  'flex min-h-14 flex-col items-center justify-center gap-0.5 rounded-full text-caption font-semibold active:bg-field-on-overlay',
                  isActive
                    ? 'bg-field-on-overlay text-text-primary'
                    : 'text-text-tertiary',
                )}
              >
                <Icon className="size-6" aria-hidden="true" />
                {t(labelKey)}
              </Link>
            );
          })}
        </div>
      </nav>
    </>
  );
}
