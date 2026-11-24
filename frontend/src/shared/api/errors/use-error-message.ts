import { useTranslations } from 'next-intl';

import { ApiError } from './api-error';

/**
 * Translated fallback text for an error whose code the feature does not handle itself.
 */
export function useErrorMessage() {
  const t = useTranslations('errors');

  return (error: unknown): string => {
    if (!(error instanceof ApiError)) return t('unexpected');
    switch (error.status) {
      case 400:
        return t('badRequest');
      case 401:
        return t('sessionExpired');
      case 403:
        return t('forbidden');
      case 404:
        return t('notFound');
      case 409:
        return t('conflict');
      case 503:
        return t('unreachable');
    }
    return error.status >= 500 ? t('server') : t('unexpected');
  };
}

/**
 * Translated text for a field error code: the text for this field, else the one for the code,
 * else a generic one. Never returns the raw code.
 */
export function useFieldErrorMessage() {
  const t = useTranslations('validation');

  return (field: string, code: string): string => {
    const fieldKey = `${field}.${code}` as Parameters<typeof t>[0];
    if (t.has(fieldKey)) return t(fieldKey);
    const codeKey = code as Parameters<typeof t>[0];
    if (t.has(codeKey)) return t(codeKey);
    return t('fallback');
  };
}
