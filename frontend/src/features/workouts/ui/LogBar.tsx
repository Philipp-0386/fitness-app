import { useTranslations } from 'next-intl';

import { Button } from '@/shared/ui/button';
import { useFormatSetValues } from '../lib/format-set';
import { requiredFields, type SetRow } from '../lib/set-rows';
import type { WorkoutExercise } from '../types/api.types';

type LogBarProps = {
  entry: WorkoutExercise;
  rows: SetRow[];
  isLast: boolean;
  onLog: (row: SetRow) => void;
  onNext: () => void;
  onFinish: () => void;
};

//logs the first open row; without one it moves on to the next exercise, or finishes on the last
export default function LogBar({
  entry,
  rows,
  isLast,
  onLog,
  onNext,
  onFinish,
}: LogBarProps) {
  const t = useTranslations('workouts.log');
  const formatSetValues = useFormatSetValues();
  const type = entry.exercise.trackingType;
  const index = rows.findIndex((r) => r.status === 'open');

  if (index === -1) {
    return (
      <Button
        type="button"
        variant="primary"
        size="lg"
        aria-haspopup={isLast ? 'dialog' : undefined}
        onClick={isLast ? onFinish : onNext}
      >
        {isLast ? t('finish') : t('nextExercise')}
      </Button>
    );
  }

  const row = rows[index];
  const workingNumber = rows
    .slice(0, index + 1)
    .filter((r) => r.setType === 'WORKING').length;
  const missing = requiredFields[type].some((field) => row[field] === null);

  return (
    <Button
      type="button"
      variant="primary"
      size="lg"
      disabled={missing}
      onClick={() => onLog(row)}
      className="tabular-nums"
    >
      {t(row.setType, { number: workingNumber, values: formatSetValues(type, row) })}
    </Button>
  );
}
