import { useTranslations } from 'next-intl';

//on desktop the detail pane next to the history list, on phones the list fills the page alone
export default function HistoryPage() {
  const t = useTranslations('workouts.history');

  return (
    <div className="hidden flex-col items-center justify-center px-4 text-center lg:flex">
      <h2 className="text-title">{t('noneSelected')}</h2>
      <p className="mt-1 text-text-secondary">{t('noneSelectedText')}</p>
    </div>
  );
}
