'use client';

import { useState } from 'react';
import Link from 'next/link';
import { Construction, X } from 'lucide-react';

import { GITHUB_URL, WIP_BANNER_COOKIE, WIP_BANNER_VERSION } from '@/shared/site';
import styles from './wipBanner.module.css';

const ONE_YEAR_IN_SECONDS = 60 * 60 * 24 * 365;

export default function WipBanner() {
  const [isVisible, setIsVisible] = useState(true);

  function dismiss() {
    document.cookie = `${WIP_BANNER_COOKIE}=${WIP_BANNER_VERSION}; path=/; max-age=${ONE_YEAR_IN_SECONDS}; samesite=lax`;
    setIsVisible(false);
  }

  if (!isVisible) return null;

  return (
    <aside className={styles.banner} aria-label="Project status">
      <Construction className={styles.icon} aria-hidden="true" />
      <p className={styles.text}>
        <strong>Work in progress.</strong> This is a solo side project under active
        development. Features may change and data may reset at any time.{' '}
        <Link href="/about" className={styles.link}>
          Learn more
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
      <button
        type="button"
        className={styles.dismiss}
        onClick={dismiss}
        aria-label="Dismiss notice"
      >
        <X className={styles.dismissIcon} aria-hidden="true" />
      </button>
    </aside>
  );
}
