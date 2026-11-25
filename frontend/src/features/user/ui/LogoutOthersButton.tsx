'use client';

import { useState } from 'react';
import { MonitorSmartphone } from 'lucide-react';
import { useTranslations } from 'next-intl';
import { toast } from 'sonner';

import { cn } from '@/shared/lib/utils';
import { listRowClass } from '@/shared/ui/list';
import { logoutOthers } from '../client/logout-others.client';

export default function LogoutOthersButton() {
  const [isPending, setIsPending] = useState(false);
  const t = useTranslations('me.logoutOthers');

  async function handleClick() {
    if (isPending) return;
    setIsPending(true);

    try {
      await logoutOthers();
      toast.success(t('done'));
    } catch {
      toast.error(t('failed'));
    } finally {
      setIsPending(false);
    }
  }

  return (
    <button
      type="button"
      onClick={handleClick}
      disabled={isPending}
      className={cn(
        listRowClass,
        'disabled:pointer-events-none disabled:text-text-disabled',
      )}
    >
      <MonitorSmartphone className="size-5 shrink-0" aria-hidden="true" />
      <span className="min-w-0 flex-1 font-semibold">
        {isPending ? t('submitting') : t('submit')}
      </span>
    </button>
  );
}
