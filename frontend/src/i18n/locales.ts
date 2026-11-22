export const locales = ['en', 'de'] as const;

export type AppLocale = (typeof locales)[number];
