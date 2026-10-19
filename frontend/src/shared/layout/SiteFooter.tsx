import Link from 'next/link';

import { GITHUB_URL } from '@/shared/site';

const linkClass =
  'flex min-h-tap items-center px-2 text-meta text-text-secondary hover:text-text-primary';

export default function SiteFooter() {
  return (
    <footer className="border-t border-border-subtle">
      <div className="mx-auto max-w-app px-4 pt-4 pb-6">
        <nav aria-label="Site" className="-mx-2 mt-1 flex flex-wrap justify-center">
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
      </div>
    </footer>
  );
}
