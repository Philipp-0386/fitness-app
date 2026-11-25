'use client';

import Link from 'next/link';
import { Info, X } from 'lucide-react';
import { useTranslations } from 'next-intl';
import { useState } from 'react';

type WipBannerProps = {
  dismissible?: boolean;
};

export default function WipBanner({ dismissible = false }: WipBannerProps) {
  const [hidden, setHidden] = useState(false);
  const t = useTranslations('wipBanner');
  const tNav = useTranslations('nav');
  if (hidden) return null;

  return (
    <aside aria-label={t('label')} className="bg-surface text-caption text-text-tertiary">
      <div className="mx-auto flex max-w-app items-center gap-2 px-4 py-2">
        <Info className="size-4 shrink-0" aria-hidden="true" />
        <p className="flex-1">{t('text')}</p>
        <Link
          href="/about"
          className="relative text-text-secondary underline underline-offset-2 before:absolute before:-inset-x-2 before:-inset-y-3.5"
        >
          {tNav('about')}
        </Link>
        {dismissible && (
          <button
            type="button"
            aria-label={t('hide')}
            onClick={() => {
              document.cookie = 'wip_hidden=1; max-age=172800; path=/; SameSite=Lax';
              setHidden(true);
            }}
            className="relative ml-2 text-text-secondary before:absolute before:-inset-3"
          >
            <X className="size-4" aria-hidden="true" />
          </button>
        )}
      </div>
    </aside>
  );
}
