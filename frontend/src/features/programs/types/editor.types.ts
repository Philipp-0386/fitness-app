import type { Routine } from '@/features/routines';

/** One row of the program editor. The key is client side, the same routine may appear twice. */
export type DayDraft = {
  key: string;
  /** null for a rest day. */
  routine: Routine | null;
};
