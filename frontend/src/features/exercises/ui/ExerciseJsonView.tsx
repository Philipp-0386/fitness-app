import { ApiError } from '@/shared/api/errors/api-error';
import { NetworkError } from '@/shared/api/errors/network-error';
import { fetchExerciseById, fetchExercises } from '../api/exercises.api';

type Props = {
  /** Reads a single exercise when set, the whole catalog otherwise. */
  id?: string;
};

export default async function ExerciseJsonView({ id }: Props) {
  const payload = await readPayload(id);

  return (
    <section>
      <h2>{id ? `exercise ${id}` : 'exercise catalog'}</h2>
      <pre>{payload}</pre>
    </section>
  );
}

/** Returns the response, or a failed request's details, as formatted JSON. */
async function readPayload(id?: string): Promise<string> {
  try {
    const data = id ? await fetchExerciseById(id) : await fetchExercises();
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

function format(data: unknown): string {
  return JSON.stringify(data, null, 2);
}
