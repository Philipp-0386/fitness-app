import Link from 'next/link';
import { ChevronDown } from 'lucide-react';

//later: 1 of x, timer, finish
export default function WorkoutTopBar() {
  return (
    <header className="sticky top-(--app-top-inset) z-10 flex h-14 items-center bg-bg px-4">
      <Link
        href="/dashboard"
        aria-label="Minimize workout"
        className="-ml-2 flex size-tap items-center justify-center text-text-secondary hover:text-text-primary"
      >
        <ChevronDown className="size-6" aria-hidden="true" />
      </Link>
    </header>
  );
}
