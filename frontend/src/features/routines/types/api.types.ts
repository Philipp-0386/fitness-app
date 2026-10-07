// Mirrors backends RoutineResponse record.
export type Routine = {
  id: number;
  name: string;
  description: string | null;
  createdAt: string;
  updatedAt: string;
};
