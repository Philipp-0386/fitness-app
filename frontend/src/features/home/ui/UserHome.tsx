import Link from 'next/link';
import { Dumbbell, ClipboardList, LineChart, Plus } from 'lucide-react';

import LogoutButton from './LogoutButton';
import styles from './userHome.module.css';

type UserHomeProps = {
  username: string | null;
};

// Placeholder values until the corresponding backend endpoints exist.
const stats = [
  { label: 'Sessions this week', value: '-' },
  { label: 'Active plans', value: '-' },
  { label: 'Volume this week', value: '-' },
];

const shortcuts = [
  {
    icon: Plus,
    title: 'Start a workout',
    text: 'Log a new training session.',
    href: '/',
  },
  {
    icon: ClipboardList,
    title: 'Training plans',
    text: 'Create and edit your routines.',
    href: '/',
  },
  {
    icon: Dumbbell,
    title: 'Exercises',
    text: 'Browse the exercise catalog.',
    href: '/',
  },
  {
    icon: LineChart,
    title: 'Progress',
    text: 'Review your history and records.',
    href: '/',
  },
];

export default function UserHome({ username }: UserHomeProps) {
  return (
    <main className={styles.page}>
      <header className={styles.header}>
        <div>
          <p className={styles.eyebrow}>Welcome back</p>
          <h1 className={styles.title}>{username ?? 'Athlete'}</h1>
        </div>
        <LogoutButton />
      </header>

      <section className={styles.stats}>
        {stats.map(({ label, value }) => (
          <article key={label} className={styles.statCard}>
            <p className={styles.statValue}>{value}</p>
            <p className={styles.statLabel}>{label}</p>
          </article>
        ))}
      </section>

      <section>
        <h2 className={styles.sectionTitle}>Quick actions</h2>
        <div className={styles.shortcuts}>
          {shortcuts.map(({ icon: Icon, title, text, href }) => (
            <Link key={title} href={href} className={styles.shortcutCard}>
              <Icon className={styles.shortcutIcon} aria-hidden="true" />
              <span className={styles.shortcutTitle}>{title}</span>
              <span className={styles.shortcutText}>{text}</span>
            </Link>
          ))}
        </div>
      </section>
    </main>
  );
}
