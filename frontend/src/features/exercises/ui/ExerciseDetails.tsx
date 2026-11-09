import { cn } from '@/shared/lib/utils';
import { trackingOptions } from '../lib/tracking-options';
import { typeLabels } from '../lib/type-labels';
import { Exercise } from '../types/api.types';

//read only rows: flat and lower than tappable rows, so they do not look like controls
const infoListClass = 'divide-y divide-field-on-overlay';
const infoRowClass = 'flex items-center gap-3 py-2.5';

type ExerciseDetailsProps = {
  exercise: Exercise;
};

//everything about an exercise below its title, shared by the detail page and the sheet in a routine
export default function ExerciseDetails({ exercise }: ExerciseDetailsProps) {
  const tracking = trackingOptions.find((o) => o.value === exercise.trackingType);

  return (
    <>
      {exercise.custom && (
        <p className="mt-1">
          <span className="rounded-full border border-border-on-overlay px-2 text-caption font-semibold text-text-secondary">
            Yours
          </span>
        </p>
      )}

      {exercise.description && (
        <p className="mt-3 max-w-[62ch] text-text-secondary">{exercise.description}</p>
      )}

      <dl className={cn(infoListClass, 'mt-5')}>
        <div className={infoRowClass}>
          <dt className="min-w-0 flex-1 text-text-secondary">Type</dt>
          <dd className="shrink-0 font-semibold">{typeLabels[exercise.exerciseType]}</dd>
        </div>
        {tracking && (
          <div className={infoRowClass}>
            <dt className="min-w-0 flex-1 text-text-secondary">Logged per set</dt>
            <dd className="shrink-0 font-semibold">{tracking.label}</dd>
          </div>
        )}
      </dl>

      <section className="mt-6" aria-labelledby="muscles-title">
        <h2 id="muscles-title" className="text-title">
          Muscles
        </h2>
        {exercise.muscles.length > 0 ? (
          <ul className={cn(infoListClass, 'mt-3')}>
            {exercise.muscles.map((muscle) => (
              <li key={muscle.id} className={infoRowClass}>
                <span className="min-w-0 flex-1 text-text-secondary">
                  {muscle.role === 'PRIMARY' ? 'Primary' : 'Secondary'}
                </span>
                <span className="shrink-0 font-semibold">{muscle.name}</span>
              </li>
            ))}
          </ul>
        ) : (
          <p className="mt-1.5 text-text-secondary">No muscles assigned yet.</p>
        )}
      </section>

      <section className="mt-6" aria-labelledby="instructions-title">
        <h2 id="instructions-title" className="text-title">
          Instructions
        </h2>
        <p className="mt-1.5 max-w-[62ch] whitespace-pre-line text-text-secondary">
          {exercise.instructions || 'No instructions yet.'}
        </p>
      </section>
    </>
  );
}
