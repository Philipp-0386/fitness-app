'use client';

import * as React from 'react';
import { cn } from '@/shared/lib/utils';

const SWIPE_CLOSE_DISTANCE = 100;
const SWIPE_CLOSE_VELOCITY = 0.5;

//wrapper with styling for dialog element, a click on the scrim closes it like Escape
function Sheet({
  className,
  style,
  onClose,
  children,
  ...props
}: React.ComponentProps<'dialog'>) {
  const pressedOutside = React.useRef(false);
  const [dragY, setDragY] = React.useState(0);
  const [dragging, setDragging] = React.useState(false);
  const [closing, setClosing] = React.useState(false);
  const [sheetHeight, setSheetHeight] = React.useState(Infinity);
  const drag = React.useRef<{
    startY: number;
    lastY: number;
    lastTime: number;
    velocity: number;
  } | null>(null);

  const dim = closing ? 0 : 1 - dragY / sheetHeight;

  return (
    <dialog
      data-slot="sheet"
      onMouseDown={(e) => {
        const rect = e.currentTarget.getBoundingClientRect();
        pressedOutside.current =
          e.clientX < rect.left ||
          e.clientX > rect.right ||
          e.clientY < rect.top ||
          e.clientY > rect.bottom;
      }}
      onClick={(e) => {
        // requestClose fires the cancel event first, so a caller can still prevent closing.
        if (pressedOutside.current && e.target === e.currentTarget) {
          e.currentTarget.requestClose();
        }
      }}
      onClose={(e) => {
        drag.current = null;
        setClosing(false);
        setDragY(0);
        onClose?.(e);
      }}
      className={cn(
        'fixed inset-x-0 top-auto bottom-0 mx-auto max-h-[85dvh] w-full max-w-app overflow-y-auto rounded-t-sheet bg-surface-overlay px-4 pt-2 pb-[calc(20px+env(safe-area-inset-bottom))] text-text-primary shadow-[0_-8px_32px_rgb(0_0_0/0.5)] backdrop:bg-[rgb(0_0_0/calc(0.6*var(--sheet-dim,1)))] open:animate-in open:animation-duration-220 open:ease-standard open:fade-in open:slide-in-from-bottom-7 motion-reduce:open:animate-none lg:inset-0 lg:m-auto lg:h-fit lg:max-w-120 lg:rounded-sheet lg:p-6 lg:shadow-[0_8px_32px_rgb(0_0_0/0.5)] lg:open:animation-duration-180 lg:open:slide-in-from-bottom-0 lg:open:zoom-in-97',
        '[&_[data-slot=input]]:border-border-on-overlay [&_[data-slot=input]]:bg-field-on-overlay [&_[data-slot=input]]:placeholder:text-text-secondary [&_[data-variant=secondary]]:border-border-on-overlay',
        !dragging &&
          'transition-[translate] duration-750 ease-standard backdrop:transition-[background-color] backdrop:duration-750 backdrop:ease-standard motion-reduce:transition-none motion-reduce:backdrop:transition-none',
        className,
      )}
      style={
        {
          ...style,
          translate: closing ? '0 calc(100% + 40px)' : `0 ${dragY}px`,
          '--sheet-dim': dim,
        } as React.CSSProperties
      }
      {...props}
    >
      {/* drag strip: a long enough pull or a quick flick down closes the sheet */}
      <div
        className="-mx-4 -mt-2 flex touch-none justify-center pt-4 pb-6 select-none lg:hidden"
        onPointerDown={(e) => {
          drag.current = {
            startY: e.clientY,
            lastY: e.clientY,
            lastTime: e.timeStamp,
            velocity: 0,
          };
          setSheetHeight(e.currentTarget.closest('dialog')?.offsetHeight ?? Infinity);
          e.currentTarget.setPointerCapture(e.pointerId);
          setDragging(true);
        }}
        onPointerMove={(e) => {
          const g = drag.current;
          if (!g) return;
          const dt = e.timeStamp - g.lastTime;
          if (dt > 0) g.velocity = (e.clientY - g.lastY) / dt;
          g.lastY = e.clientY;
          g.lastTime = e.timeStamp;
          setDragY(Math.max(0, e.clientY - g.startY));
        }}
        onPointerUp={(e) => {
          const g = drag.current;
          setDragging(false);
          if (!g) return;
          const velocity = e.timeStamp - g.lastTime > 100 ? 0 : g.velocity;
          const shouldClose =
            velocity > SWIPE_CLOSE_VELOCITY ||
            (dragY > SWIPE_CLOSE_DISTANCE && velocity > -SWIPE_CLOSE_VELOCITY);
          const dialog = e.currentTarget.closest('dialog');
          // dispatch cancel first like requestClose does, so a caller can still prevent closing
          if (
            shouldClose &&
            dialog?.dispatchEvent(new Event('cancel', { cancelable: true }))
          ) {
            if (window.matchMedia('(prefers-reduced-motion: reduce)').matches) {
              dialog.close();
            } else {
              setClosing(true);
              // close as soon as the sheet is out of view, so the page is not blocked for the rest of the slide
              const closeWhenOutOfView = () => {
                if (!dialog.isConnected || !dialog.open) return;
                if (dialog.getBoundingClientRect().top >= window.innerHeight) {
                  dialog.close();
                } else {
                  requestAnimationFrame(closeWhenOutOfView);
                }
              };
              requestAnimationFrame(closeWhenOutOfView);
            }
            return;
          }
          drag.current = null;
          setDragY(0);
        }}
        onPointerCancel={() => {
          drag.current = null;
          setDragging(false);
          setDragY(0);
        }}
      >
        <div aria-hidden="true" className="h-1 w-9 rounded-full bg-border-strong" />
      </div>
      {children}
    </dialog>
  );
}

export { Sheet };
