'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { ChevronRight } from 'lucide-react';

import { cn } from '@/shared/lib/utils';
import { listClass, listRowClass } from '@/shared/ui/list';
import { Routine } from '../types/api.types';

type RoutineListProps = {
  routines: Routine[];
  /** Route the routine id is appended to, e.g. /plans/routines or /plans/routines/presets. */
  hrefBase: string;
};

export default function RoutineList({ routines, hrefBase }: RoutineListProps) {
  const pathname = usePathname();

  return (
    <ul className={listClass}>
      {routines.map((routine) => {
        const href = `${hrefBase}/${routine.id}`;
        const isActive = pathname === href || pathname.startsWith(`${href}/`);
        return (
          <li key={routine.id}>
            <Link
              href={href}
              aria-current={isActive ? 'page' : undefined}
              className={cn(listRowClass, isActive && 'lg:bg-surface-raised')}
            >
              <span className="min-w-0 flex-1">
                <span className="block truncate font-semibold">{routine.name}</span>
                <span className="block text-meta text-text-tertiary tabular-nums">
                  {routine.exerciseCount}{' '}
                  {routine.exerciseCount === 1 ? 'exercise' : 'exercises'}
                </span>
              </span>
              <ChevronRight
                className="size-5 shrink-0 text-text-tertiary"
                aria-hidden="true"
              />
            </Link>
          </li>
        );
      })}
    </ul>
  );
}
