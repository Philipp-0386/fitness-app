'use client';

import { useLocale } from 'next-intl';
import { useRouter } from 'next/navigation';
import { useState } from 'react';
import { toast } from 'sonner';

import { fallbackErrorMessage } from '@/shared/api/errors/fallback-error-message';
import { toApiError } from '@/shared/api/errors/to-api-error';
import { cn } from '@/shared/lib/utils';

const options = [
  { value: 'en', label: 'EN' },
  { value: 'de', label: 'DE' },
] as const;

export function LanguageSwitch() {
  const locale = useLocale();
  const router = useRouter();
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
      toast.error(fallbackErrorMessage(err));
    } finally {
      setIsSaving(false);
    }
  }

  return (
    <div
      role="radiogroup"
      aria-label="Language"
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
