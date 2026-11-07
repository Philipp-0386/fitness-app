'use client';

import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useState } from 'react';
import { toast } from 'sonner';

import { ApiError } from '@/shared/api/errors/api-error';
import { fallbackErrorMessage } from '@/shared/api/errors/fallback-error-message';
import { Button } from '@/shared/ui/button';
import { startWorkout } from '../client/start-workout.client';

type StartWorkoutButtonProps = {
  routineId: number;
  /** The workout in progress. null if there is none. */
  running: { name: string; routineId: number | null } | null;
};

export default function StartWorkoutButton({
  routineId,
  running,
}: StartWorkoutButtonProps) {
  const [isStarting, setIsStarting] = useState(false);
  const router = useRouter();

  async function handleStart() {
    if (isStarting) return;
    setIsStarting(true);
    try {
      await startWorkout({ routineId });
      router.push('/workout');
    } catch (err) {
      if (err instanceof ApiError && err.status === 409) {
        router.push('/workout');
        return;
      }
      setIsStarting(false);
      toast.error(fallbackErrorMessage(err));
    }
  }

  if (running) {
    return (
      <div>
        <Button asChild variant="primary" size="lg">
          <Link href="/workout">Resume {running.name}</Link>
        </Button>
        {running.routineId !== routineId && (
          <p className="mt-2 text-meta text-text-secondary">
            Another workout is running. Finish it first.
          </p>
        )}
      </div>
    );
  }

  return (
    <Button
      type="button"
      variant="primary"
      size="lg"
      onClick={handleStart}
      disabled={isStarting}
    >
      {isStarting ? 'Starting...' : 'Start Workout'}
    </Button>
  );
}
