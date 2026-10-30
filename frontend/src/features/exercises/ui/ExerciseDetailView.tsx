import Link from 'next/link';

import { TopBar } from '@/shared/layout';
import { Button } from '@/shared/ui/button';
import { Exercise } from '../types/api.types';
import ExerciseDetails from './ExerciseDetails';

type ExerciseDetailViewProps = {
  exercise: Exercise;
};

//own exercises get an Edit button, global ones are read only
export default function ExerciseDetailView({ exercise }: ExerciseDetailViewProps) {
  return (
    <div className="mx-auto flex w-full max-w-app flex-1 flex-col pb-8 lg:max-w-[560px]">
      <TopBar
        backHref="/exercises"
        backLabel="Exercises"
        title={exercise.name}
        inSplitView
        action={
          exercise.custom && (
            <Button asChild size="sm">
              <Link href={`/exercises/${exercise.id}/edit`} replace>
                Edit
              </Link>
            </Button>
          )
        }
      />

      <div className="px-4">
        <ExerciseDetails exercise={exercise} />
      </div>
    </div>
  );
}
