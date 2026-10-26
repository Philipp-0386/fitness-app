/** Mirrors the backend RoutineSummary record. */
export type ProgramDayRoutine = {
  id: number;
  name: string;
  /** In routine order, soft deleted exercises included. */
  exerciseNames: string[];
};

/** Mirrors the backend ProgramDayResponse record. routine is null for a rest day. */
export type ProgramDay = {
  id: number;
  orderIndex: number;
  routine: ProgramDayRoutine | null;
};

/** Mirrors the backend ProgramResponse record. */
export type Program = {
  id: number;
  name: string;
  description: string | null;
  active: boolean;
  dayCount: number;
  createdAt: string;
  updatedAt: string;
};

/** Mirrors the backend ProgramDetailResponse record. */
export type ProgramDetail = {
  id: number;
  name: string;
  description: string | null;
  active: boolean;
  createdAt: string;
  updatedAt: string;
  days: ProgramDay[];
};
