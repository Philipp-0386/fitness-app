import { TopBar } from '@/shared/layout';
import { ProgramDetail } from '../types/api.types';
import DeleteProgramDialog from './DeleteProgramDialog';
import ProgramDayList from './ProgramDayList';

type ProgramDetailViewProps = {
  program: ProgramDetail;
};

export default function ProgramDetailView({ program }: ProgramDetailViewProps) {
  return (
    <div className="mx-auto flex w-full max-w-app flex-1 flex-col pb-8 lg:max-w-[560px]">
      <TopBar backHref="/plans" backLabel="Plans" title={program.name} inSplitView />

      <p className="mt-1 px-4 text-meta text-text-secondary tabular-nums">
        {program.active ? 'Active program' : 'Not active'}, {program.days.length}{' '}
        {program.days.length === 1 ? 'day' : 'days'}, repeats
      </p>
      {program.description && (
        <p className="mt-2 px-4 text-text-secondary">{program.description}</p>
      )}

      <div className="mx-4 mt-5">
        <ProgramDayList program={program} exercises="names" />
      </div>

      <div className="mx-4 mt-8 border-t border-border-subtle pt-2">
        <DeleteProgramDialog programId={program.id} active={program.active} />
      </div>
    </div>
  );
}
