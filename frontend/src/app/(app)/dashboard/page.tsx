import { UserHome } from '@/features/home';
import { fetchActiveProgram } from '@/features/programs';
import { fetchMe } from '@/features/user';
import { requireSession } from '@/shared/auth/session';
import { fetchActiveWorkout } from '@/features/workouts';

export default async function DashboardPage() {
  await requireSession();

  const [username, program, activeWorkout] = await Promise.all([
    fetchMe()
      .then((user) => user.username)
      .catch(() => null),
    fetchActiveProgram().catch((error: Error) => error),
    fetchActiveWorkout().catch(() => null),
  ]);

  return <UserHome username={username} program={program} activeWorkout={activeWorkout} />;
}
