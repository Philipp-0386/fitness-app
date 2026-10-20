import * as React from 'react';
import { cn } from '@/shared/lib/utils';

//wrapper with styling for dialog element
function Sheet({ className, children, ...props }: React.ComponentProps<'dialog'>) {
  return (
    <dialog
      data-slot="sheet"
      className={cn(
        'fixed inset-x-0 top-auto bottom-0 mx-auto max-h-[85dvh] w-full max-w-app overflow-y-auto rounded-t-sheet bg-surface-overlay px-4 pt-2 pb-[calc(20px+env(safe-area-inset-bottom))] text-text-primary shadow-[0_-8px_32px_rgb(0_0_0/0.5)] backdrop:bg-[rgb(0_0_0/0.6)] open:animate-in open:duration-220 open:ease-standard open:fade-in open:slide-in-from-bottom-7 motion-reduce:open:animate-none lg:inset-0 lg:m-auto lg:h-fit lg:max-w-120 lg:rounded-sheet lg:p-6 lg:shadow-[0_8px_32px_rgb(0_0_0/0.5)] lg:open:duration-180 lg:open:slide-in-from-bottom-0 lg:open:zoom-in-97',
        '[&_[data-slot=input]]:border-border-on-overlay [&_[data-slot=input]]:bg-field-on-overlay [&_[data-slot=input]]:placeholder:text-text-secondary [&_[data-variant=secondary]]:border-border-on-overlay',
        className,
      )}
      {...props}
    >
      <div
        aria-hidden="true"
        className="mx-auto mt-1 mb-3.5 h-1 w-9 rounded-full bg-border-strong lg:hidden"
      />
      {children}
    </dialog>
  );
}

export { Sheet };
