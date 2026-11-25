import { useTranslations } from 'next-intl';

import { ApiError } from './api-error';

/**
 * Key in the errors messages for an error whose code the feature does not handle itself.
 */
export function fallbackErrorKey(error: unknown) {
  if (!(error instanceof ApiError)) return 'unexpected';
  switch (error.status) {
    case 400:
      return 'badRequest';
    case 401:
      return 'sessionExpired';
    case 403:
      return 'forbidden';
    case 404:
      return 'notFound';
    case 409:
      return 'conflict';
    case 503:
      return 'unreachable';
  }
  return error.status >= 500 ? 'server' : 'unexpected';
}

/**
 * Translated fallback text for an error whose code the feature does not handle itself.
 * Async server components use getErrorMessage instead.
 */
export function useErrorMessage() {
  const t = useTranslations('errors');

  return (error: unknown): string => t(fallbackErrorKey(error));
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
