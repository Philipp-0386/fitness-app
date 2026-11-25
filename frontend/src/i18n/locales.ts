export const locales = ['en-GB', 'de'] as const;

export type AppLocale = (typeof locales)[number];
