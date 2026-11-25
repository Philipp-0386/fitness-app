import { useTranslations } from 'next-intl';

import { TopBar } from '@/shared/layout';

//rendered next to the catalog when the page calls notFound(), e.g. for a deleted exercise
export default function ExerciseNotFound() {
  const t = useTranslations('exercises');

  return (
    <div className="mx-auto flex w-full max-w-app flex-1 flex-col pb-8 lg:max-w-[560px]">
      <TopBar
        backHref="/exercises"
        backLabel={t('title')}
        title={t('notFound')}
        inSplitView
      />

      <p className="mt-1 px-4 text-text-secondary">{t('notFoundText')}</p>
    </div>
  );
}
