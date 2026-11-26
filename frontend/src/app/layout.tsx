import type { Metadata, Viewport } from 'next';
import { Archivo } from 'next/font/google';
import { CircleAlert, CircleCheck } from 'lucide-react';
import { Toaster } from 'sonner';

import { NextIntlClientProvider } from 'next-intl';
import { getLocale, getTranslations } from 'next-intl/server';

import './globals.css';
import styles from './layout.module.css';

const archivo = Archivo({
  variable: '--font-archivo',
  subsets: ['latin'],
});

export async function generateMetadata(): Promise<Metadata> {
  const t = await getTranslations('layout');

  return {
    title: t('title'),
    description: t('description'),
    appleWebApp: {
      title: t('title'),
      statusBarStyle: 'black-translucent',
    },
  };
}

export const viewport: Viewport = {
  viewportFit: 'cover',
  maximumScale: 1,
  userScalable: false,
};

export default async function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  const locale = await getLocale();

  return (
    <html lang={locale} className={`dark ${archivo.variable}`}>
      <body>
        <NextIntlClientProvider>
          <div className={styles.content}>{children}</div>
          <Toaster
            position="bottom-center"
            duration={2600}
            offset={{ bottom: 'calc(88px + env(safe-area-inset-bottom))' }}
            mobileOffset={{ bottom: 'calc(88px + env(safe-area-inset-bottom))' }}
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
                actionButton:
                  '-my-3 -mr-2 ml-auto min-h-tap shrink-0 px-2 font-semibold text-text-primary',
              },
            }}
          />
        </NextIntlClientProvider>
      </body>
    </html>
  );
}
