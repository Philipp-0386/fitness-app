import Link from 'next/link';
import { Dumbbell, LineChart, CalendarCheck } from 'lucide-react';

import styles from './guestHome.module.css';

const features = [
  {
    icon: Dumbbell,
    title: 'Plan your workouts',
    text: 'Build routines from an exercise catalog and reuse them week after week.',
  },
  {
    icon: CalendarCheck,
    title: 'Log every session',
    text: 'Track sets, reps and weights while you train - no spreadsheet needed.',
  },
  {
    icon: LineChart,
    title: 'See your progress',
    text: 'Watch your volume and personal records develop over time.',
  },
];

export default function GuestHome() {
  return (
    <main className={styles.page}>
      <section className={styles.hero}>
        <p className={styles.eyebrow}>Fitness App</p>
        <h1 className={styles.title}>
          Train with a plan, <span className={styles.highlight}>not with a guess</span>
        </h1>
        <p className={styles.subtitle}>
          Create your training plans, log your sessions and keep track of your progress -
          all in one place.
        </p>
        <div className={styles.actions}>
          <Link href="/signup" className={styles.primaryAction}>
            Get started
          </Link>
          <Link href="/login" className={styles.secondaryAction}>
            Log in
          </Link>
        </div>
      </section>

      <section className={styles.features}>
        {features.map(({ icon: Icon, title, text }) => (
          <article key={title} className={styles.featureCard}>
            <Icon className={styles.featureIcon} aria-hidden="true" />
            <h2 className={styles.featureTitle}>{title}</h2>
            <p className={styles.featureText}>{text}</p>
          </article>
        ))}
      </section>
    </main>
  );
}
