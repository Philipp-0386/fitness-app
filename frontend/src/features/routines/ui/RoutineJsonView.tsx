import { ApiError } from '@/shared/api/errors/api-error';
import { NetworkError } from '@/shared/api/errors/network-error';
import {
  fetchPresetRoutineByRoutineId,
  fetchPresetRoutines,
  fetchUserRoutineByRoutineId,
  fetchUserRoutines,
} from '../api/routines.api';

export type RoutineSource = 'mine' | 'presets';

type Props = {
  /** Reads the user's own routines or the presets. */
  source: RoutineSource;
  /** Reads a single routine when set, the whole list otherwise. */
  id?: string;
};

export default async function RoutineJsonView({ source, id }: Props) {
  const payload = await readPayload(source, id);

  return (
    <section>
      <h2>{id ? `${source} routine ${id}` : `${source} routines`}</h2>
      <pre>{payload}</pre>
    </section>
  );
}

/** Returns the response, or a failed request's details, as formatted JSON. */
async function readPayload(source: RoutineSource, id?: string): Promise<string> {
  try {
    const data = await load(source, id);
    return format(data);
  } catch (error) {
    if (error instanceof ApiError) {
      return format({
        status: error.status,
        code: error.code,
        message: error.message,
        path: error.path,
        timestamp: error.timestamp,
      });
    }
    if (error instanceof NetworkError) {
      return `no response: ${error.message}`;
    }
    throw error;
  }
}

function load(source: RoutineSource, id?: string) {
  if (source === 'presets') {
    return id ? fetchPresetRoutineByRoutineId(id) : fetchPresetRoutines();
  }
  return id ? fetchUserRoutineByRoutineId(id) : fetchUserRoutines();
}

function format(data: unknown): string {
  return JSON.stringify(data, null, 2);
}
