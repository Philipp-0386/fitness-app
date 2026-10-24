import { TopBar } from '@/shared/layout';

//rendered next to the catalog when the page calls notFound(), e.g. for a deleted exercise
export default function ExerciseNotFound() {
  return (
    <div className="mx-auto flex w-full max-w-app flex-1 flex-col pb-8 lg:max-w-[560px]">
      <TopBar
        backHref="/exercises"
        backLabel="Exercises"
        title="Exercise not found"
        inSplitView
      />

      <p className="mt-1 px-4 text-text-secondary">
        This exercise does not exist (anymore). It may have been deleted, or the link is
        wrong.
      </p>
    </div>
  );
}
