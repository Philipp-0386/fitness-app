import { LockKeyhole } from 'lucide-react';

import styles from './signUpClosedBanner.module.css';

export default function SignUpClosedBanner() {
  return (
    <aside className={styles.banner} aria-label="Sign-up status">
      <LockKeyhole className={styles.icon} aria-hidden="true" />
      <p className={styles.text}>
        <strong>Sign-ups are currently closed.</strong> New accounts cannot be created at
        the moment. Please check back later.
      </p>
    </aside>
  );
}
