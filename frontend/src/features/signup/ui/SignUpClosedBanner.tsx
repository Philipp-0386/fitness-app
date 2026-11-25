import { LockKeyhole } from 'lucide-react';
import { useTranslations } from 'next-intl';

export default function SignUpClosedBanner() {
  const t = useTranslations('signup');

  return (
    <aside
      aria-label={t('closedLabel')}
      className="mb-8 flex items-start gap-3 rounded-card bg-surface p-4 text-meta text-text-secondary"
    >
      <LockKeyhole className="mt-0.5 size-4 shrink-0" aria-hidden="true" />
      <p>
        <strong className="font-semibold text-text-primary">{t('closedTitle')}</strong>{' '}
        {t('closedText')}
      </p>
    </aside>
  );
}
