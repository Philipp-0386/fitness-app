import Link from 'next/link';
import { Construction } from 'lucide-react';

import { GITHUB_URL } from '@/shared/site';
import styles from './wipBanner.module.css';

export default function WipBanner() {
  return (
    <aside className={styles.banner} aria-label="Project status">
      <Construction className={styles.icon} aria-hidden="true" />
      <p className={styles.text}>
        <strong>Work in progress.</strong> This is a solo side project and data may reset
        any day.{' '}
        <Link href="/about" className={styles.link}>
          About
        </Link>
        {' | '}
        <a
          href={GITHUB_URL}
          className={styles.link}
          target="_blank"
          rel="noopener noreferrer"
        >
          GitHub
        </a>
      </p>
    </aside>
  );
}
