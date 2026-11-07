'use client';

import { useEffect, useRef, useState } from 'react';

import { cn } from '@/shared/lib/utils';

const ACTION_WIDTH = 96;

type SwipeableRowProps = {
  /** Classes of the sliding content, e.g. its grid and background. */
  className?: string;
  actionLabel: string;
  onAction: () => void;
  children: React.ReactNode;
};

//list row that reveals a destructive action when swiped left (touch only); the action needs a second tap
export default function SwipeableRow({
  className,
  actionLabel,
  onAction,
  children,
}: SwipeableRowProps) {
  const [offset, setOffset] = useState(0);
  const [dragging, setDragging] = useState(false);
  const rowRef = useRef<HTMLLIElement>(null);
  const gesture = useRef<{
    x: number;
    y: number;
    offset: number;
    axis: 'x' | 'y' | null;
  } | null>(null);
  const swallowClick = useRef(false);

  const open = offset === -ACTION_WIDTH && !dragging;

  useEffect(() => {
    if (!open) return;
    function closeOutside(e: PointerEvent) {
      if (!rowRef.current?.contains(e.target as Node)) setOffset(0);
    }
    document.addEventListener('pointerdown', closeOutside);
    return () => document.removeEventListener('pointerdown', closeOutside);
  }, [open]);

  return (
    <li ref={rowRef} className="relative overflow-hidden rounded-card">
      <button
        type="button"
        tabIndex={open ? 0 : -1}
        aria-hidden={!open}
        onClick={() => {
          setOffset(0);
          onAction();
        }}
        className="absolute inset-y-0 right-0 font-semibold text-bg bg-danger"
        style={{ width: ACTION_WIDTH }}
      >
        {actionLabel}
      </button>

      <div
        className={cn(
          'relative isolate touch-pan-y bg-bg',
          !dragging &&
            'transition-transform duration-180 ease-standard motion-reduce:transition-none',
          className,
        )}
        style={{ transform: `translateX(${offset}px)` }}
        onPointerDown={(e) => {
          if (e.pointerType === 'mouse') return;
          gesture.current = { x: e.clientX, y: e.clientY, offset, axis: null };
        }}
        onPointerMove={(e) => {
          const g = gesture.current;
          if (!g) return;
          const dx = e.clientX - g.x;
          const dy = e.clientY - g.y;
          if (g.axis === null) {
            if (Math.abs(dx) < 8 && Math.abs(dy) < 8) return;
            g.axis = Math.abs(dx) > Math.abs(dy) ? 'x' : 'y';
            if (g.axis === 'x') {
              e.currentTarget.setPointerCapture(e.pointerId);
              setDragging(true);
            }
          }
          if (g.axis !== 'x') return;
          setOffset(Math.min(0, Math.max(-ACTION_WIDTH * 1.5, g.offset + dx)));
        }}
        onPointerUp={() => {
          const g = gesture.current;
          gesture.current = null;
          if (g?.axis !== 'x') return;
          swallowClick.current = true;
          setDragging(false);
          setOffset(offset < -ACTION_WIDTH / 2 ? -ACTION_WIDTH : 0);
        }}
        onPointerCancel={() => {
          gesture.current = null;
          setDragging(false);
          setOffset(0);
        }}
        onClickCapture={(e) => {
          if (swallowClick.current || offset !== 0) {
            e.stopPropagation();
            e.preventDefault();
            swallowClick.current = false;
            setOffset(0);
          }
        }}
      >
        {children}
      </div>
    </li>
  );
}
