'use client';

import { ChevronRight } from 'lucide-react';
import Link from 'next/link';
import { useTranslations } from 'next-intl';
import { useState } from 'react';

import ExerciseSheet from '@/features/exercises/ui/ExerciseSheet';
import type { Exercise } from '@/features/exercises';
import { TopBar } from '@/shared/layout';
import { cn } from '@/shared/lib/utils';
import { Button } from '@/shared/ui/button';
import { listClass, listRowClass } from '@/shared/ui/list';
import { useFormatTarget } from '../lib/format-target';
import { RoutineDetail } from '../types/api.types';

type RoutineDetailViewProps = {
  routine: RoutineDetail;
  /** The catalog, a tapped row looks its exercise up here to show it in a sheet. */
  exercises: Exercise[];
  /** Rendered below the exercises, starts or resumes a workout. (not sure if permanent) */
  startAction: React.ReactNode;
};

export default function RoutineDetailView({
  routine,
  exercises,
  startAction,
}: RoutineDetailViewProps) {
  const [openExercise, setOpenExercise] = useState<Exercise | null>(null);
  const t = useTranslations('routines');
  const tCommon = useTranslations('common');
  const tNav = useTranslations('nav');
  const formatTarget = useFormatTarget();

  return (
    <div className="mx-auto flex w-full max-w-app flex-1 flex-col pb-8 lg:max-w-[560px]">
      <TopBar
        backHref="/plans"
        backLabel={tNav('plans')}
        title={routine.name}
        inSplitView
        action={
          <Button asChild size="sm">
            <Link href={`/plans/routines/${routine.id}/edit`} replace>
              {tCommon('edit')}
            </Link>
          </Button>
        }
      />

      {routine.description && (
        <p className="mt-1 px-4 text-text-secondary">{routine.description}</p>
      )}

      {routine.exercises.length === 0 ? (
        <p className="mt-5 px-4 text-text-secondary">{t('empty')}</p>
      ) : (
        <ul className={cn(listClass, 'mx-4 mt-5')}>
          {routine.exercises.map((re) => {
            const type = re.exercise.trackingType;
            const sub = re.exercise.deleted
              ? t('deletedExercise')
              : type === 'BODYWEIGHT_REPS' || type === 'ASSISTED_REPS'
                ? t(`trackingHint.${type}`)
                : undefined;
            const exercise = re.exercise.deleted
              ? undefined
              : exercises.find((e) => e.id === re.exercise.id);
            const content = (
              <>
                <span className="min-w-0 flex-1">
                  <span className="block font-semibold">{re.exercise.name}</span>
                  {sub && (
                    <span className="block text-meta text-text-tertiary">{sub}</span>
                  )}
                </span>
                <span className="shrink-0 text-meta text-text-secondary tabular-nums">
                  {formatTarget(re)}
                </span>
              </>
            );
            return exercise ? (
              <li key={re.id}>
                <button
                  type="button"
                  aria-haspopup="dialog"
                  onClick={() => setOpenExercise(exercise)}
                  className={listRowClass}
                >
                  {content}
                  <ChevronRight
                    className="size-5 shrink-0 text-text-tertiary"
                    aria-hidden="true"
                  />
                </button>
              </li>
            ) : (
              <li key={re.id} className="flex min-h-14 items-center gap-3 px-4 py-2.5">
                {content}
              </li>
            );
          })}
        </ul>
      )}

      <div className="mx-4 mt-6">{startAction}</div>
      {openExercise && (
        <ExerciseSheet exercise={openExercise} onClose={() => setOpenExercise(null)} />
      )}
    </div>
  );
}
