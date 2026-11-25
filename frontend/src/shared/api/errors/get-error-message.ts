import 'server-only';

import { getTranslations } from 'next-intl/server';

import { fallbackErrorKey } from './use-error-message';

/**
 * useErrorMessage for async server components, which cant call hooks.
 */
export async function getErrorMessage(error: unknown): Promise<string> {
  const t = await getTranslations('errors');
  return t(fallbackErrorKey(error));
}
