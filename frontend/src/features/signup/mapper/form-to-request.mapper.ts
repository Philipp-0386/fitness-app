import type { Locale } from 'next-intl';

import { SignUpFormValues } from '../types/form.types';
import { SignUpPayload } from '../types/api.types';

export function mapFormToPayload(form: SignUpFormValues, locale: Locale): SignUpPayload {
  return {
    username: form.username.trim(),
    email: form.email.trim(),
    password: form.password,
    language: locale === 'de' ? 'DE' : 'EN',
  };
}
