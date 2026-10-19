import Link from 'next/link';
import { Info } from 'lucide-react';

export default function WipBanner() {
  return (
    <aside
      aria-label="Project status"
      className="bg-surface text-caption text-text-tertiary"
    >
      <div className="mx-auto flex max-w-app items-center gap-2 px-4 py-2">
        <Info className="size-4 shrink-0" aria-hidden="true" />
        <p className="flex-1">Work in progress. Data may reset any day.</p>
        <Link
          href="/about"
          className="relative text-text-secondary underline underline-offset-2 before:absolute before:-inset-x-2 before:-inset-y-3.5"
        >
          About
        </Link>
      </div>
    </aside>
  );
}
