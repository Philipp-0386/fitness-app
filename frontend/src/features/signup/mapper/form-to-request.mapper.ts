import { SignUpFormValues } from '../types/form.types';
import { SignUpPayload } from '../types/api.types';

export function mapFormToPayload(form: SignUpFormValues): SignUpPayload {
  const dateOfBirth = form.dateOfBirth.trim();

  return {
    username: form.username.trim(),
    email: form.email.trim(),
    password: form.password,
    firstName: form.firstName.trim(),
    lastName: form.lastName.trim(),
    ...(dateOfBirth ? { dateOfBirth } : {}),
  };
}
