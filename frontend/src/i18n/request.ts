import { cookies, headers } from 'next/headers';
import { getRequestConfig } from 'next-intl/server';

import { fetchSettings } from '@/features/user';
import { getSession } from '@/shared/auth/session';

import type { AppLocale } from './locales';
import { LOCALE_COOKIE } from '@/shared/auth/cookies';

async function resolveLocale(): Promise<AppLocale> {
  if (await getSession()) {
    try {
      const settings = await fetchSettings();
      return settings.language === 'DE' ? 'de' : 'en';
    } catch {
      // Settings unreachable: fall back to the guest resolution instead of failing the page.
    }
  }

  const cookieLocale = (await cookies()).get(LOCALE_COOKIE)?.value;
  if (cookieLocale === 'en' || cookieLocale === 'de') return cookieLocale;

  const acceptLanguage = (await headers()).get('accept-language') ?? '';
  return acceptLanguage.toLowerCase().startsWith('de') ? 'de' : 'en';
}

export default getRequestConfig(async () => {
  const locale = await resolveLocale();

  return {
    locale,
    timeZone: 'Europe/Berlin',
    messages: (await import(`../../messages/${locale}.json`)).default,
  };
});
