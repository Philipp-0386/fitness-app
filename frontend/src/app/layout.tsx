import type { Metadata, Viewport } from 'next';
import { Archivo } from 'next/font/google';
import { CircleAlert, CircleCheck } from 'lucide-react';
import { Toaster } from 'sonner';

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

export const viewport: Viewport = { viewportFit: 'cover' };

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en" className={`dark ${archivo.variable}`}>
      <body>
        <div className={styles.content}>{children}</div>
        <Toaster
          position="bottom-center"
          duration={2600}
          offset={{ bottom: 'calc(72px + env(safe-area-inset-bottom))' }}
          mobileOffset={{ bottom: 'calc(72px + env(safe-area-inset-bottom))' }}
          icons={{
            success: <CircleCheck className="size-5 text-success" aria-hidden="true" />,
            error: <CircleAlert className="size-5 text-danger" aria-hidden="true" />,
          }}
          toastOptions={{
            unstyled: true,
            classNames: {
              toast:
                'flex w-full items-center gap-3 rounded-card bg-surface-overlay px-4 py-3.5 font-sans text-body text-text-primary shadow-[0_8px_24px_rgb(0_0_0/0.5)]',
              icon: 'flex shrink-0',
            },
          }}
        />
      </body>
    </html>
  );
}
