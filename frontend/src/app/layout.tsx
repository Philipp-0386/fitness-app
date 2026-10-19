import type { Metadata } from 'next';
import { Archivo } from 'next/font/google';
import { Toaster } from 'sonner';

import { SiteFooter, WipBanner } from '@/shared/layout';
import './globals.css';
import styles from './layout.module.css';

const archivo = Archivo({
  variable: '--font-archivo',
  subsets: ['latin'],
});

export const metadata: Metadata = {
  title: 'Fitness App',
  description: 'Plan your workouts, log your sessions and track your progress.',
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en" className={`dark ${archivo.variable}`}>
      <body>
        <WipBanner />
        <div className={styles.content}>{children}</div>
        <SiteFooter />
        <Toaster richColors position="top-right" />
      </body>
    </html>
  );
}
