import { TopBar } from '@/shared/layout';
import { trackingOptions } from '../lib/tracking-options';
import { typeLabels } from '../lib/type-labels';
import { Exercise } from '../types/api.types';
import DeleteExerciseDialog from './DeleteExerciseDialog';
import ExerciseFormDialog from './ExerciseFormDialog';

type ExerciseDetailViewProps = {
  exercise: Exercise;
};

//own exercises get an Edit button and can be deleted, global ones are read only
export default function ExerciseDetailView({ exercise }: ExerciseDetailViewProps) {
  const tracking = trackingOptions.find((o) => o.value === exercise.trackingType);

  return (
    <div className="mx-auto flex w-full max-w-app flex-1 flex-col pb-8 lg:max-w-[560px]">
      <TopBar
        backHref="/exercises"
        backLabel="Exercises"
        title={exercise.name}
        inSplitView
        action={exercise.custom && <ExerciseFormDialog exercise={exercise} />}
      />

      <p className="mt-1 flex flex-wrap items-center gap-2 px-4 text-text-secondary">
        {typeLabels[exercise.exerciseType]}
        {tracking && `, ${tracking.label.toLowerCase()}`}
        {exercise.custom && (
          <span className="rounded-full border border-border-strong px-2 text-caption font-semibold text-text-secondary">
            Yours
          </span>
        )}
      </p>

      {exercise.description && (
        <p className="mt-3 max-w-[62ch] px-4 text-text-secondary">
          {exercise.description}
        </p>
      )}

      <section className="mt-6 px-4" aria-labelledby="instructions-title">
        <h2 id="instructions-title" className="text-title">
          Instructions
        </h2>
        <p className="mt-1.5 max-w-[62ch] whitespace-pre-line text-text-secondary">
          {exercise.instructions || 'No instructions yet.'}
        </p>
      </section>

      {exercise.custom && (
        <div className="mt-8 px-4">
          <DeleteExerciseDialog exerciseId={exercise.id} />
        </div>
      )}
    </div>
  );
}
