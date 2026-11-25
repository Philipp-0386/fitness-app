import { useTranslations } from 'next-intl';

//on desktop the detail pane next to the catalog, on phones the catalog fills the page alone
export default function ExercisesPage() {
  const t = useTranslations('exercises');

  return (
    <div className="hidden flex-col items-center justify-center px-4 text-center lg:flex">
      <h2 className="text-title">{t('noneSelected')}</h2>
      <p className="mt-1 text-text-secondary">{t('noneSelectedText')}</p>
    </div>
  );
}
