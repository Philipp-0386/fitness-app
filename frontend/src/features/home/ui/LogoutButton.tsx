'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { LogOut } from 'lucide-react';
import { toast } from 'sonner';

import { Button } from '@/shared/ui/button';

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
    <Button type="button" onClick={handleLogout} disabled={isLoggingOut}>
      <LogOut aria-hidden="true" />
      {isLoggingOut ? 'Logging out...' : 'Log out'}
    </Button>
  );
}
