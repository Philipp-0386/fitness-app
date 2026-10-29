'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { ChevronRight } from 'lucide-react';

import { cn } from '@/shared/lib/utils';
import { listClass, listRowClass } from '@/shared/ui/list';
import { Program } from '../types/api.types';

type ProgramListProps = {
  programs: Program[];
};

export default function ProgramList({ programs }: ProgramListProps) {
  const pathname = usePathname();

  return (
    <ul className={listClass}>
      {programs.map((program) => {
        const href = `/plans/programs/${program.id}`;
        const isActive = pathname === href || pathname.startsWith(`${href}/`);
        return (
          <li key={program.id}>
            <Link
              href={href}
              aria-current={isActive ? 'page' : undefined}
              className={cn(listRowClass, isActive && 'lg:bg-surface-raised')}
            >
              <span className="min-w-0 flex-1 truncate font-semibold">
                {program.name}
              </span>
              {program.active && (
                <span className="shrink-0 text-meta font-semibold">Active</span>
              )}
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
