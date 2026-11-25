'use client';

import { ChevronDown } from 'lucide-react';
import { useTranslations } from 'next-intl';

import { cn } from '@/shared/lib/utils';
import { exerciseTypes } from '../lib/exercise-types';
import { ExerciseType } from '../types/api.types';

type ExerciseTypeFilterProps = {
  value: 'all' | ExerciseType;
  onChange: (value: 'all' | ExerciseType) => void;
};

//the chip is only the visible part, an invisible native select on top of it opens the system picker
export default function ExerciseTypeFilter({ value, onChange }: ExerciseTypeFilterProps) {
  const t = useTranslations('exercises');
  const selected = value !== 'all';

  return (
    <span
      className={cn(
        'relative inline-flex h-8.5 w-28 shrink-0 items-center justify-between gap-1.5 rounded-full border px-3 text-meta font-semibold whitespace-nowrap has-focus-visible:outline-2 has-focus-visible:outline-offset-2 has-focus-visible:outline-focus',
        selected
          ? 'border-text-primary bg-surface-overlay text-text-primary'
          : 'border-border-strong text-text-secondary',
      )}
    >
      {selected ? t(`type.${value}`) : t('typeFilter.label')}
      <ChevronDown className="size-4" aria-hidden="true" />
      <select
        aria-label={t('typeFilter.label')}
        value={value}
        onChange={(e) => onChange(e.target.value as 'all' | ExerciseType)}
        className="absolute inset-x-0 -inset-y-[5px] cursor-pointer bg-surface-overlay text-base text-text-primary opacity-0"
      >
        <option value="all">{t('typeFilter.all')}</option>
        {exerciseTypes.map((type) => (
          <option key={type} value={type}>
            {t(`type.${type}`)}
          </option>
        ))}
      </select>
    </span>
  );
}
