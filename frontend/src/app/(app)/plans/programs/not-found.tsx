import { useTranslations } from 'next-intl';

import { TopBar } from '@/shared/layout';

//rendered inside the plans layout when a program page calls notFound(), e.g. for a deleted program
export default function ProgramNotFound() {
  const t = useTranslations('programs');
  const tNav = useTranslations('nav');

  return (
    <div className="mx-auto flex w-full max-w-app flex-1 flex-col pb-8 lg:max-w-[560px]">
      <TopBar
        backHref="/plans"
        backLabel={tNav('plans')}
        title={t('notFound')}
        inSplitView
      />

      <p className="mt-1 px-4 text-text-secondary">{t('notFoundText')}</p>
    </div>
  );
}
