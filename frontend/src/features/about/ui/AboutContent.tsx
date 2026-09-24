import { GITHUB_ISSUES_URL, GITHUB_URL } from '@/shared/site';
import styles from './aboutContent.module.css';

export default function AboutContent() {
  return (
    <main className={styles.page}>
      <article className={styles.article}>
        <p className={styles.eyebrow}>About</p>
        <h1 className={styles.title}>About this app</h1>

        <p className={styles.lead}>
          This app is a personal project, designed, built and hosted by me. It is still in
          early development, so expect rough edges, missing features and the occasional
          bug.
        </p>

        <section className={styles.notice}>
          <h2 className={styles.noticeTitle}>Please note</h2>
          <ul className={styles.list}>
            <li>
              Your data may be reset while the app is still evolving. Do not rely on it as
              your only training log for now.
            </li>
            <li>The app is free and non-commercial. There are no ads and no tracking.</li>
            <li>
              This is <strong>not medical or professional</strong> training advice.
            </li>
          </ul>
        </section>

        <p className={styles.text}>
          Found a bug or have an idea? I´d love to hear it:{' '}
          <a
            href={GITHUB_ISSUES_URL}
            className={styles.link}
            target="_blank"
            rel="noopener noreferrer"
          >
            open an issue on GitHub
          </a>
          .
        </p>

        <p className={styles.text}>
          The source code is available on{' '}
          <a
            href={GITHUB_URL}
            className={styles.link}
            target="_blank"
            rel="noopener noreferrer"
          >
            GitHub
          </a>
          .
        </p>
      </article>
    </main>
  );
}
