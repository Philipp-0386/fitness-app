'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { ChevronRight } from 'lucide-react';

import { cn } from '@/shared/lib/utils';
import { listClass, listRowClass } from '@/shared/ui/list';
import { ProgramDetail } from '../types/api.types';

type ProgramDayListProps = {
  program: ProgramDetail;
  /** What a training day shows below its routine name. */
  exercises: 'names' | 'count';
};

export default function ProgramDayList({ program, exercises }: ProgramDayListProps) {
  const pathname = usePathname();

  return (
    <ol className={listClass} aria-label={`Days of ${program.name}`}>
      {program.days.map((day, index) => {
        const number = (
          <span
            className="w-5 shrink-0 text-center font-semibold text-text-tertiary tabular-nums"
            aria-hidden="true"
          >
            {index + 1}
          </span>
        );

        if (!day.routine) {
          return (
            <li key={day.id} className="flex min-h-14 items-center gap-3 px-4 py-2.5">
              {number}
              <span className="font-semibold text-text-secondary">Rest day</span>
            </li>
          );
        }

        const href = `/plans/routines/${day.routine.id}`;
        const isActive = pathname === href || pathname.startsWith(`${href}/`);
        const names = day.routine.exerciseNames;
        return (
          <li key={day.id}>
            <Link
              href={href}
              aria-current={isActive ? 'page' : undefined}
              className={cn(listRowClass, isActive && 'lg:bg-surface-raised')}
            >
              {number}
              <span className="min-w-0 flex-1">
                <span className="block truncate font-semibold">{day.routine.name}</span>
                <span className="line-clamp-2 text-meta text-text-tertiary tabular-nums">
                  {names.length === 0
                    ? 'No exercises yet'
                    : exercises === 'names'
                      ? names.join(', ')
                      : `${names.length} ${names.length === 1 ? 'exercise' : 'exercises'}`}
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
    </ol>
  );
}
