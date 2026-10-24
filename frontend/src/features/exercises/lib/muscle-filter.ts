import { Exercise, MuscleRole } from '../types/api.types';

export type MuscleFilter = {
  groupId: number;
  /** null filters by the whole group. */
  areaId: number | null;
  /** Name of the picked group or area. */
  label: string;
  role: MuscleRole | 'EITHER';
};

/** True without a filter, or if a muscle of the filtered role is the picked area or lies in the picked group. */
export function matchesMuscleFilter(exercise: Exercise, filter: MuscleFilter | null) {
  return (
    !filter ||
    exercise.muscles.some(
      (m) =>
        (filter.role === 'EITHER' || m.role === filter.role) &&
        (filter.areaId !== null ? m.id === filter.areaId : m.groupId === filter.groupId),
    )
  );
}
