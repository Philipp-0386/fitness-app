import { TopBar } from '@/shared/layout';

//rendered inside the plans layout when a page calls notFound(), e.g. for a deleted routine
export default function RoutineNotFound() {
  return (
    <div className="mx-auto flex w-full max-w-app flex-1 flex-col pb-8 lg:max-w-[560px]">
      <TopBar
        backHref="/routines"
        backLabel="Plans"
        title="Routine not found"
        inSplitView
      />

      <p className="mt-1 px-4 text-text-secondary">
        This routine does not exist (anymore). It may have been deleted, or the link is
        wrong.
      </p>
    </div>
  );
}
