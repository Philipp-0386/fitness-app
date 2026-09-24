import type { Metadata } from 'next';
import { Geist, Geist_Mono } from 'next/font/google';
import { cookies } from 'next/headers';
import { Toaster } from 'sonner';

import { SiteFooter, WipBanner } from '@/shared/layout';
import { WIP_BANNER_COOKIE, WIP_BANNER_VERSION } from '@/shared/site';
import './globals.css';
import styles from './layout.module.css';

const geistSans = Geist({
  variable: '--font-geist-sans',
  subsets: ['latin'],
});

const geistMono = Geist_Mono({
  variable: '--font-geist-mono',
  subsets: ['latin'],
});

export const metadata: Metadata = {
  title: 'Fitness App',
  description: 'Plan your workouts, log your sessions and track your progress.',
};

export default async function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  const cookieStore = await cookies();
  const isBannerDismissed =
    cookieStore.get(WIP_BANNER_COOKIE)?.value === WIP_BANNER_VERSION;

  return (
    <html lang="en">
      <body className={`${geistSans.variable} ${geistMono.variable}`}>
        {!isBannerDismissed && <WipBanner />}
        <div className={styles.content}>{children}</div>
        <SiteFooter />
        <Toaster richColors position="top-right" />
      </body>
    </html>
  );
}
