'use client';

import Link from 'next/link';
import { useEffect, useRef } from 'react';

import { Button } from '@/shared/ui/button';
import { Sheet } from '@/shared/ui/sheet';
import { Exercise } from '../types/api.types';
import ExerciseDetails from './ExerciseDetails';

type ExerciseSheetProps = {
  exercise: Exercise;
  onClose: () => void;
};

//quick look at an exercise without leaving the page; the caller mounts it while open
export default function ExerciseSheet({ exercise, onClose }: ExerciseSheetProps) {
  const dialogRef = useRef<HTMLDialogElement>(null);

  useEffect(() => {
    dialogRef.current?.showModal();
  }, []);

  return (
    <Sheet ref={dialogRef} onClose={onClose} aria-labelledby="exercise-sheet-title">
      <div className="flex items-start justify-between gap-3">
        <h2 id="exercise-sheet-title" className="text-title">
          {exercise.name}
        </h2>
        <Button
          type="button"
          variant="text"
          size="sm"
          onClick={() => dialogRef.current?.close()}
        >
          Done
        </Button>
      </div>

      <ExerciseDetails exercise={exercise} />

      {exercise.custom && (
        <Button asChild className="mt-6 w-full">
          <Link href={`/exercises/${exercise.id}/edit`}>Edit Exercise</Link>
        </Button>
      )}
    </Sheet>
  );
}
