import { useTranslations } from 'next-intl';

/**
 * Shown while the server component of this route awaits the backend.
 */
export default function Loading() {
  const t = useTranslations('exercises');

  return (
    <div className="mx-auto flex w-full max-w-app flex-1 flex-col px-4 pt-18 pb-8 lg:max-w-[560px]">
      <p className="text-text-secondary">{t('loading')}</p>
    </div>
  );
}
