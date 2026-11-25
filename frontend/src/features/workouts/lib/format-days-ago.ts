import { useTranslations } from 'next-intl';

/** "today", "yesterday", "3 days ago", "2 weeks ago", "4 months ago" or "a year ago", counted in calendar days. */
export function useFormatDaysAgo() {
  const t = useTranslations('workouts.ago');

  return (iso: string): string => {
    const now = new Date();
    const then = new Date(iso);
    const days = Math.round(
      (new Date(now.getFullYear(), now.getMonth(), now.getDate()).getTime() -
        new Date(then.getFullYear(), then.getMonth(), then.getDate()).getTime()) /
        86_400_000,
    );
    if (days <= 0) return t('today');
    if (days === 1) return t('yesterday');
    if (days < 14) return t('days', { count: days });
    if (days < 60) return t('weeks', { count: Math.floor(days / 7) });
    if (days < 365) return t('months', { count: Math.floor(days / 30) });
    return t('years', { count: Math.floor(days / 365) });
  };
}
