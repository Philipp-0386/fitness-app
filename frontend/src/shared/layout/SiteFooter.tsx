import Link from 'next/link';

import { GITHUB_URL } from '@/shared/site';

const linkClass =
  'flex min-h-tap items-center px-2 text-meta text-text-secondary hover:text-text-primary';

export default function SiteFooter() {
  return (
    <footer className="border-t border-border-subtle">
      <nav
        aria-label="Site"
        className="mx-auto flex max-w-app flex-wrap justify-center px-2 py-1"
      >
        <Link href="/about" className={linkClass}>
          About
        </Link>
        <Link href="/datenschutz" className={linkClass}>
          Datenschutz/Privacy Policy
        </Link>
        <a
          href={GITHUB_URL}
          className={linkClass}
          target="_blank"
          rel="noopener noreferrer"
        >
          GitHub<span className="sr-only"> (opens in a new tab)</span>
        </a>
      </nav>
    </footer>
  );
}
