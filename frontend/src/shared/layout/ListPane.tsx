'use client';

import { useSelectedLayoutSegment } from 'next/navigation';

import { cn } from '@/shared/lib/utils';

//left pane of a list and detail split view, on phones only shown while no detail is open
export default function ListPane({ children }: { children: React.ReactNode }) {
  const segment = useSelectedLayoutSegment();

  return (
    <div
      className={cn(
        'flex-1 flex-col lg:sticky lg:top-0 lg:flex lg:h-dvh lg:self-start lg:overflow-y-auto lg:border-r lg:border-border-subtle',
        segment === null ? 'flex' : 'hidden',
      )}
    >
      {children}
    </div>
  );
}
