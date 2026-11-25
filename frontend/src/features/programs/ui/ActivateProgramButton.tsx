'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { useTranslations } from 'next-intl';
import { toast } from 'sonner';

import { useErrorMessage } from '@/shared/api/errors/use-error-message';
import { Button } from '@/shared/ui/button';
import { activateProgram } from '../client/activate-program.client';

type ActivateProgramButtonProps = {
  programId: number;
};

export default function ActivateProgramButton({ programId }: ActivateProgramButtonProps) {
  const [isActivating, setIsActivating] = useState(false);
  const router = useRouter();
  const t = useTranslations('programs');
  const errorMessage = useErrorMessage();

  async function handleActivate() {
    if (isActivating) return;
    setIsActivating(true);
    try {
      await activateProgram(programId);
      toast.success(t('activated'));
      router.refresh();
    } catch (err) {
      setIsActivating(false);
      toast.error(errorMessage(err));
    }
  }

  return (
    <Button type="button" size="sm" onClick={handleActivate} disabled={isActivating}>
      {isActivating ? t('activating') : t('activate')}
    </Button>
  );
}
