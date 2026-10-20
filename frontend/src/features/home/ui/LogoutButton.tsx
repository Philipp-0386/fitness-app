'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { LogOut } from 'lucide-react';
import { toast } from 'sonner';

import { cn } from '@/shared/lib/utils';
import { listRowClass } from '@/shared/ui/list';

export default function LogoutButton() {
  const [isLoggingOut, setIsLoggingOut] = useState(false);
  const router = useRouter();

  async function handleLogout() {
    if (isLoggingOut) return;
    setIsLoggingOut(true);

    try {
      const response = await fetch('/api/logout', { method: 'POST' });
      if (!response.ok) {
        toast.error('Logout failed. Please try again.');
        return;
      }
      router.replace('/');
      router.refresh();
    } catch {
      toast.error('Logout failed. Please try again.');
    } finally {
      setIsLoggingOut(false);
    }
  }

  return (
    <button
      type="button"
      onClick={handleLogout}
      disabled={isLoggingOut}
      className={cn(
        listRowClass,
        'disabled:pointer-events-none disabled:text-text-disabled',
      )}
    >
      <LogOut className="size-5 shrink-0" aria-hidden="true" />
      <span className="min-w-0 flex-1 font-semibold">
        {isLoggingOut ? 'Logging out...' : 'Log out'}
      </span>
    </button>
  );
}
