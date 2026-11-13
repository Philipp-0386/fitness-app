import { TopBar } from '@/shared/layout';

//rendered inside the history layout when a page calls notFound(), e.g. for a deleted workout
export default function WorkoutNotFound() {
  return (
    <div className="mx-auto flex w-full max-w-app flex-1 flex-col pb-8 lg:max-w-[560px]">
      <TopBar
        backHref="/history"
        backLabel="History"
        title="Workout not found"
        inSplitView
      />

      <p className="mt-1 px-4 text-text-secondary">
        This workout does not exist (anymore). It may have been deleted, or the link is
        wrong.
      </p>
    </div>
  );
}
