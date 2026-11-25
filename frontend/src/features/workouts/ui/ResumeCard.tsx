import Link from 'next/link';
import { useTranslations } from 'next-intl';

import { Button } from '@/shared/ui/button';
import type { Workout } from '../types/api.types';
import ElapsedClock from './ElapsedClock';

type ResumeCardProps = {
  workout: Workout;
};

export default function ResumeCard({ workout }: ResumeCardProps) {
  const t = useTranslations('workouts');

  return (
    <section
      className="mt-3 rounded-card bg-surface px-4 pt-5 pb-4"
      aria-labelledby="resume-title"
    >
      <h2 id="resume-title" className="text-title">
        {t('inProgress', { name: workout.name })}
      </h2>
      <p className="mt-1 text-text-secondary">
        <ElapsedClock startedAt={workout.startedAt} />
      </p>
      <Button asChild variant="primary" size="lg" className="mt-4">
        <Link href="/workout">{t('resume')}</Link>
      </Button>
    </section>
  );
}
