import { useTranslations } from 'next-intl';

import { TopBar } from '@/shared/layout';

//rendered inside the history layout when a page calls notFound(), e.g. for a deleted workout
export default function WorkoutNotFound() {
  const t = useTranslations('workouts');

  return (
    <div className="mx-auto flex w-full max-w-app flex-1 flex-col pb-8 lg:max-w-[560px]">
      <TopBar
        backHref="/history"
        backLabel={t('history.title')}
        title={t('notFound')}
        inSplitView
      />

      <p className="mt-1 px-4 text-text-secondary">{t('notFoundText')}</p>
    </div>
  );
}
