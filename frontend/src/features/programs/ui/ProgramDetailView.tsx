import Link from 'next/link';
import { useTranslations } from 'next-intl';

import { TopBar } from '@/shared/layout';
import { Button } from '@/shared/ui/button';
import { ProgramDetail } from '../types/api.types';
import ActivateProgramButton from './ActivateProgramButton';
import ProgramDayList from './ProgramDayList';

type ProgramDetailViewProps = {
  program: ProgramDetail;
};

export default function ProgramDetailView({ program }: ProgramDetailViewProps) {
  const t = useTranslations('programs');
  const tCommon = useTranslations('common');
  const tNav = useTranslations('nav');

  return (
    <div className="mx-auto flex w-full max-w-app flex-1 flex-col pb-8 lg:max-w-[560px]">
      <TopBar
        backHref="/plans"
        backLabel={tNav('plans')}
        title={program.name}
        inSplitView
        action={
          <Button asChild size="sm">
            <Link href={`/plans/programs/${program.id}/edit`} replace>
              {tCommon('edit')}
            </Link>
          </Button>
        }
        titleAction={!program.active && <ActivateProgramButton programId={program.id} />}
      />

      <p className="mt-1 px-4 text-meta text-text-secondary tabular-nums">
        {t('summary', {
          active: program.active ? 'yes' : 'no',
          count: program.days.length,
        })}
      </p>
      {program.description && (
        <p className="mt-2 px-4 text-text-secondary">{program.description}</p>
      )}

      <div className="mx-4 mt-5">
        <ProgramDayList program={program} exercises="names" />
      </div>
    </div>
  );
}
