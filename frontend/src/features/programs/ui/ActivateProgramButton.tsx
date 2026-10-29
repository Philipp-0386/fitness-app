'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { toast } from 'sonner';

import { fallbackErrorMessage } from '@/shared/api/errors/fallback-error-message';
import { Button } from '@/shared/ui/button';
import { activateProgram } from '../client/activate-program.client';

type ActivateProgramButtonProps = {
  programId: number;
};

export default function ActivateProgramButton({ programId }: ActivateProgramButtonProps) {
  const [isActivating, setIsActivating] = useState(false);
  const router = useRouter();

  async function handleActivate() {
    if (isActivating) return;
    setIsActivating(true);
    try {
      await activateProgram(programId);
      toast.success('Program activated.');
      router.refresh();
    } catch (err) {
      setIsActivating(false);
      toast.error(fallbackErrorMessage(err));
    }
  }

  return (
    <Button type="button" size="sm" onClick={handleActivate} disabled={isActivating}>
      {isActivating ? 'Activating...' : 'Activate'}
    </Button>
  );
}
