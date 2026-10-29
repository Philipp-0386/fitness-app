import {
  ProgramEditor,
  fetchPresetProgramByProgramId,
  fetchPresetPrograms,
} from '@/features/programs';
import {
  fetchPresetRoutineByRoutineId,
  fetchPresetRoutines,
  fetchUserRoutines,
} from '@/features/routines';
import { requireSession } from '@/shared/auth/session';

export default async function NewProgramPage() {
  await requireSession();

  const [routines, presets, presetRoutines] = await Promise.all([
    fetchUserRoutines(),
    fetchPresetPrograms().then((list) =>
      Promise.all(list.map((preset) => fetchPresetProgramByProgramId(String(preset.id)))),
    ),
    fetchPresetRoutines().then((list) =>
      Promise.all(list.map((preset) => fetchPresetRoutineByRoutineId(String(preset.id)))),
    ),
  ]);

  return (
    <ProgramEditor
      program={null}
      routines={routines}
      presets={presets}
      presetRoutines={presetRoutines}
    />
  );
}
