'use client';

import Link from 'next/link';
import { ChevronLeft } from 'lucide-react';
import { useEffect, useRef, useState } from 'react';

import { cn } from '@/shared/lib/utils';

type TopBarProps = {
  backHref: string;
  backLabel: string;
  title: string;
  /** Heads the detail pane of a split view from 1024px: no back link, the small title always visible. */
  inSplitView?: boolean;
  /** Rendered on the right of the bar, e.g. an Edit button. */
  action?: React.ReactNode;
};

//top bar of sub pages, renders the display title below the bar and shows it small in the bar once scrolled away
export default function TopBar({
  backHref,
  backLabel,
  title,
  inSplitView = false,
  action,
}: TopBarProps) {
  const titleRef = useRef<HTMLHeadingElement>(null);
  const [titleHidden, setTitleHidden] = useState(false);

  useEffect(() => {
    const heading = titleRef.current;
    if (!heading) return;

    const observer = new IntersectionObserver(
      ([entry]) => setTitleHidden(!entry.isIntersecting),
      { rootMargin: '-56px 0px 0px 0px' },
    );
    observer.observe(heading);
    return () => observer.disconnect();
  }, []);

  return (
    <>
      <header
        className={cn(
          'sticky top-0 z-10 grid h-14 grid-cols-[1fr_minmax(0,2fr)_1fr] items-center border-b bg-bg px-4 transition-colors duration-180 motion-reduce:transition-none',
          titleHidden ? 'border-border-subtle' : 'border-transparent',
          inSplitView && 'lg:flex lg:border-border-subtle',
        )}
      >
        <Link
          href={backHref}
          className={cn(
            '-ml-2 flex min-h-tap items-center gap-0.5 justify-self-start pr-2 font-semibold text-text-secondary hover:text-text-primary',
            inSplitView && 'lg:hidden',
          )}
        >
          <ChevronLeft className="size-6" aria-hidden="true" />
          {backLabel}
        </Link>
        <p
          aria-hidden="true"
          className={cn(
            'truncate text-center text-[1.0625rem] font-semibold transition-opacity duration-180 motion-reduce:transition-none',
            titleHidden ? 'opacity-100' : 'opacity-0',
            inSplitView && 'lg:text-left lg:opacity-100',
          )}
        >
          {title}
        </p>
        {action && <div className="justify-self-end lg:ml-auto">{action}</div>}
      </header>
      <h1 ref={titleRef} className="px-4 pt-4 text-display text-balance">
        {title}
      </h1>
    </>
  );
}
