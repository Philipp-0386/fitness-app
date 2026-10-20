import * as React from 'react';
import { cn } from '@/shared/lib/utils';

function Input({ className, type, ...props }: React.ComponentProps<'input'>) {
  return (
    <input
      type={type}
      data-slot="input"
      className={cn(
        'h-12 w-full min-w-0 rounded-field border border-border-strong bg-surface-raised px-3 text-field text-text-primary transition-colors duration-180 ease-standard placeholder:text-text-tertiary disabled:pointer-events-none disabled:cursor-not-allowed disabled:text-text-disabled aria-invalid:border-danger',
        className,
      )}
      {...props}
    />
  );
}

export { Input };
