import { fetchExercises, fetchMuscleGroups } from '@/features/exercises';
import {
  RoutineEditor,
  fetchPresetRoutineByRoutineId,
  fetchPresetRoutines,
} from '@/features/routines';
import { requireSession } from '@/shared/auth/session';

export default async function NewRoutinePage() {
  await requireSession();

  const [exercises, muscleGroups, presets] = await Promise.all([
    fetchExercises(),
    fetchMuscleGroups(),
    fetchPresetRoutines().then((list) =>
      Promise.all(list.map((preset) => fetchPresetRoutineByRoutineId(String(preset.id)))),
    ),
  ]);

  return (
    <RoutineEditor
      routine={null}
      presets={presets}
      exercises={exercises}
      muscleGroups={muscleGroups}
    />
  );
}
