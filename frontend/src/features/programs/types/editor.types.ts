import type { Routine } from '@/features/routines';

/** One row of the program editor. The key is client side, the same routine may appear twice. */
export type DayDraft = {
  key: string;
  /** Id of the saved day, null for a day added in the editor. */
  id: number | null;
  /** null for a rest day. */
  routine: Pick<Routine, 'id' | 'name' | 'exerciseCount'> | null;
  /** The routine is a preset, it is copied into an own routine on save. */
  fromPreset: boolean;
};
