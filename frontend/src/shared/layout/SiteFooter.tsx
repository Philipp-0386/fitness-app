import Link from 'next/link';
import { useTranslations } from 'next-intl';

import { GITHUB_URL } from '@/shared/site';

const linkClass =
  'flex min-h-tap items-center px-2 text-meta text-text-secondary hover:text-text-primary';

export default function SiteFooter() {
  const t = useTranslations('nav');

  return (
    <footer className="border-t border-border-subtle">
      <nav
        aria-label={t('site')}
        className="mx-auto flex max-w-app flex-wrap justify-center px-2 py-1"
      >
        <Link href="/about" className={linkClass}>
          {t('about')}
        </Link>
        <Link href="/datenschutz" className={linkClass}>
          {t('privacy')}
        </Link>
        <a
          href={GITHUB_URL}
          className={linkClass}
          target="_blank"
          rel="noopener noreferrer"
        >
          GitHub<span className="sr-only"> {t('opensInNewTab')}</span>
        </a>
      </nav>
    </footer>
  );
}
