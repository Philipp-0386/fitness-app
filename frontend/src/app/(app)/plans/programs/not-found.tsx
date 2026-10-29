import { TopBar } from '@/shared/layout';

//rendered inside the plans layout when a program page calls notFound(), e.g. for a deleted program
export default function ProgramNotFound() {
  return (
    <div className="mx-auto flex w-full max-w-app flex-1 flex-col pb-8 lg:max-w-[560px]">
      <TopBar
        backHref="/plans"
        backLabel="Plans"
        title="Program not found"
        inSplitView
      />

      <p className="mt-1 px-4 text-text-secondary">
        This program does not exist (anymore). It may have been deleted, or the link is
        wrong.
      </p>
    </div>
  );
}
