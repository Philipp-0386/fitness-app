/** The user's time zone, fixed until it comes from user_settings, so server and browser render the same days. */
export const TIME_ZONE = 'Europe/Berlin';

const DAY = 86_400_000;
//"2026-11-10", the calendar day of an instant in TIME_ZONE
const calendarDay = new Intl.DateTimeFormat('en-CA', {
  timeZone: TIME_ZONE,
  year: 'numeric',
  month: '2-digit',
  day: '2-digit',
});

/**
 * Groups workouts that are sorted newest first into weeks from Monday to Sunday. The label gets the Monday as
 * UTC midnight and how many weeks it lies back, 0 for this week.
 */
export function groupByWeek<T extends { startedAt: string }>(
  workouts: T[],
  label: (monday: number, weeksAgo: number) => string,
) {
  const today = Date.parse(calendarDay.format(new Date()));
  const thisMonday = today - ((new Date(today).getUTCDay() + 6) % 7) * DAY;

  const groups: { key: string; label: string; workouts: T[] }[] = [];
  for (const workout of workouts) {
    const day = Date.parse(calendarDay.format(new Date(workout.startedAt)));
    const monday = day - ((new Date(day).getUTCDay() + 6) % 7) * DAY;
    const key = new Date(monday).toISOString().slice(0, 10);

    let group = groups.at(-1);
    if (group?.key !== key) {
      group = {
        key,
        label: label(monday, Math.round((thisMonday - monday) / (7 * DAY))),
        workouts: [],
      };
      groups.push(group);
    }
    group.workouts.push(workout);
  }
  return groups;
}
