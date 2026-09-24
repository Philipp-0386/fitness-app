import Link from 'next/link';

import { GITHUB_URL } from '@/shared/site';
import styles from './siteFooter.module.css';

export default function SiteFooter() {
  return (
    <footer className={styles.footer}>
      <p className={styles.note}>Fitness App, a side project by Philipp Ringelkamp</p>
      <nav className={styles.links} aria-label="Site">
        <Link href="/about" className={styles.link}>
          About
        </Link>
        <a
          href={GITHUB_URL}
          className={styles.link}
          target="_blank"
          rel="noopener noreferrer"
        >
          GitHub
        </a>
      </nav>
    </footer>
  );
}
