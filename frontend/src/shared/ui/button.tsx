import * as React from 'react';
import { cva, type VariantProps } from 'class-variance-authority';
import { Slot } from 'radix-ui';

import { cn } from '@/shared/lib/utils';

const buttonVariants = cva(
  "inline-flex shrink-0 items-center justify-center gap-2 rounded-card font-semibold whitespace-nowrap transition-colors duration-180 ease-standard select-none disabled:pointer-events-none disabled:text-text-disabled [&_svg]:pointer-events-none [&_svg]:shrink-0 [&_svg:not([class*='size-'])]:size-5",
  {
    variants: {
      variant: {
        primary:
          'bg-action text-on-action hover:bg-action-hover active:bg-action-pressed focus-visible:shadow-[0_0_0_2px_var(--color-bg)] focus-visible:outline-offset-3 disabled:bg-surface-raised',
        secondary:
          'border border-border-strong text-text-primary hover:bg-surface-raised active:bg-surface-raised',
        text: 'text-text-secondary hover:bg-surface-raised active:bg-surface-raised',
        danger: 'bg-danger text-bg',
      },
      size: {
        default: 'min-h-tap px-4 text-body',
        sm: 'min-h-9 px-3.5 text-meta',
        lg: 'min-h-tap-primary w-full px-4 text-button',
        icon: 'size-tap',
      },
    },
    defaultVariants: {
      variant: 'secondary',
      size: 'default',
    },
  },
);

function Button({
  className,
  variant = 'secondary',
  size = 'default',
  asChild = false,
  ...props
}: React.ComponentProps<'button'> &
  VariantProps<typeof buttonVariants> & {
    asChild?: boolean;
  }) {
  const Comp = asChild ? Slot.Root : 'button';

  return (
    <Comp
      data-slot="button"
      data-variant={variant}
      data-size={size}
      className={cn(buttonVariants({ variant, size, className }))}
      {...props}
    />
  );
}

export { Button, buttonVariants };
