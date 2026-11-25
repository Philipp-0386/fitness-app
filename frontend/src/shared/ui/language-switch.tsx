'use client';

import { useLocale, useTranslations } from 'next-intl';
import { useRouter } from 'next/navigation';
import { useState } from 'react';
import { toast } from 'sonner';

import { toApiError } from '@/shared/api/errors/to-api-error';
import { useErrorMessage } from '@/shared/api/errors/use-error-message';
import { cn } from '@/shared/lib/utils';

const options = [
  { value: 'en', label: 'EN' },
  { value: 'de', label: 'DE' },
] as const;

export function LanguageSwitch() {
  const locale = useLocale();
  const router = useRouter();
  const t = useTranslations('common');
  const errorMessage = useErrorMessage();
  const [isSaving, setIsSaving] = useState(false);

  async function handleChange(next: 'en' | 'de') {
    if (next === locale) return;
    setIsSaving(true);
    try {
      const response = await fetch('/api/locale', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ locale: next }),
      });
      if (!response.ok) throw await toApiError(response);
      router.refresh();
    } catch (err) {
      toast.error(errorMessage(err));
    } finally {
      setIsSaving(false);
    }
  }

  return (
    <div
      role="radiogroup"
      aria-label={t('language')}
      className="inline-grid grid-flow-col gap-1 rounded-card bg-surface p-1"
    >
      {options.map(({ value, label }) => (
        <button
          key={value}
          type="button"
          role="radio"
          aria-checked={locale === value}
          disabled={isSaving}
          onClick={() => handleChange(value)}
          className={cn(
            'min-h-9 rounded-field px-3.5 text-meta font-semibold',
            locale === value
              ? 'bg-surface-raised text-text-primary'
              : 'text-text-tertiary hover:text-text-secondary',
          )}
        >
          {label}
        </button>
      ))}
    </div>
  );
}
