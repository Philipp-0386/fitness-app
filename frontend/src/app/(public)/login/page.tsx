import { LoginForm } from '@/features/login';
import { redirectIfAuthenticated } from '@/shared/auth/session';
import Link from 'next/link';
import { getTranslations } from 'next-intl/server';
import { LanguageSwitch } from '@/shared/ui/language-switch';

export default async function LoginPage() {
  await redirectIfAuthenticated();
  const t = await getTranslations('login');

  return (
    <main className="mx-auto flex w-full max-w-app flex-1 flex-col px-4">
      <header className="flex h-14 items-center justify-between">
        <Link href="/" className="-mx-2 flex min-h-tap items-center px-2 text-title">
          Avren
        </Link>
        <LanguageSwitch />
      </header>

      <h1 className="pt-6 pb-8 text-display">{t('title')}</h1>
      <LoginForm />

      <p className="mt-5 text-center text-meta text-text-secondary">
        {t('noAccount')}{' '}
        <Link
          href="/signup"
          className="relative font-semibold text-text-primary underline underline-offset-2 before:absolute before:-inset-x-2 before:-inset-y-3.5"
        >
          {t('signUp')}
        </Link>
      </p>
    </main>
  );
}
