import Link from 'next/link';
import { ChevronDown, List } from 'lucide-react';
import { useTranslations } from 'next-intl';

import { Button } from '@/shared/ui/button';
import ElapsedClock from './ElapsedClock';

type WorkoutTopBarProps = {
  name: string;
  startedAt: string;
  /** Index of the exercise on screen. */
  current: number;
  total: number;
  onOpenOverview: () => void;
  onFinish: () => void;
};

export default function WorkoutTopBar({
  name,
  startedAt,
  current,
  total,
  onOpenOverview,
  onFinish,
}: WorkoutTopBarProps) {
  const t = useTranslations('workouts.topBar');

  return (
    <header className="sticky top-(--app-top-inset) z-10 flex h-14 items-center gap-1 border-b border-border-subtle bg-bg px-2">
      <Link
        href="/dashboard"
        aria-label={t('minimize')}
        className="flex size-tap items-center justify-center text-text-secondary hover:text-text-primary"
      >
        <ChevronDown className="size-6" aria-hidden="true" />
      </Link>

      {total > 0 ? (
        <button
          type="button"
          onClick={onOpenOverview}
          aria-haspopup="dialog"
          aria-label={t('position', { current: current + 1, total })}
          className="inline-flex min-h-tap items-center gap-2 rounded-card px-2.5 font-semibold text-text-secondary tabular-nums active:bg-surface-raised"
        >
          <List className="size-5" aria-hidden="true" />
          {t('positionShort', { current: current + 1, total })}
        </button>
      ) : (
        <span className="min-w-0 truncate px-2.5 font-semibold text-text-secondary">
          {name}
        </span>
      )}

      <span
        role="timer"
        aria-label={t('elapsed')}
        className="mr-2 ml-auto text-[1.0625rem] font-semibold text-text-secondary"
      >
        <ElapsedClock startedAt={startedAt} />
      </span>

      <Button type="button" size="sm" aria-haspopup="dialog" onClick={onFinish}>
        {t('finish')}
      </Button>
    </header>
  );
}
