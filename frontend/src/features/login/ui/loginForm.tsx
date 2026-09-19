'use client';

import { useState } from 'react';
import { login } from '../client/login.client';
import { ApiError } from '@/shared/api/errors/api-error';
import { useRouter } from 'next/navigation';
import { Eye, EyeOff } from 'lucide-react';

import styles from './longForm.module.css';

type LoginFormValues = {
  username: string;
  password: string;
};

type FormError = Partial<Record<keyof LoginFormValues, string>>;

export default function LoginForm() {
  const [form, setForm] = useState<LoginFormValues>({ username: '', password: '' });
  const [generalError, setGeneralError] = useState<string | null>(null);
  const [fieldErrors, setFieldErrors] = useState<FormError>({});
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const router = useRouter();

  function handleChange(e: React.ChangeEvent<HTMLInputElement>) {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
    setFieldErrors((prev) => ({ ...prev, [name]: undefined }));
  }

  async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    if (isSubmitting) return;
    e.preventDefault();
    setGeneralError(null);
    setFieldErrors({});

    const errors: FormError = {};
    if (!form.username.trim()) errors.username = 'Username is required';
    if (!form.password.trim()) errors.password = 'Password is required';
    if (Object.keys(errors).length > 0) {
      setFieldErrors(errors);
      return;
    }

    setIsSubmitting(true);
    try {
      await login({ username: form.username, password: form.password });
      // Replace instead of push: the login form must not stay in the history stack.
      router.replace('/dashboard');
      router.refresh();
    } catch (error) {
      if (error instanceof ApiError) {
        if (error.code === 'INVALID_CREDENTIALS') {
          setGeneralError('Username or password is incorrect.');
        } else if (error.code === 'BACKEND_UNREACHABLE') {
          setGeneralError('The server is currently unreachable. Please try again later.');
        } else {
          setGeneralError(error.message || 'An unknown error occurred.');
        }
        return;
      }
      setGeneralError('An unexpected error occurred. Please try again later.');
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <div>
      <form onSubmit={handleSubmit} noValidate className={styles.form}>
        <div className={styles.inputBlock}>
          <label>Username:</label>
          <input
            type="text"
            name="username"
            value={form.username}
            onChange={handleChange}
            autoComplete="username"
          />
          {fieldErrors.username && (
            <p className={styles.fieldError}>{fieldErrors.username}</p>
          )}
        </div>
        <div className={styles.inputBlock}>
          <label>Password:</label>
          <div className={styles.passwordInputWrapper}>
            <input
              type={showPassword ? 'text' : 'password'}
              name="password"
              value={form.password}
              onChange={handleChange}
              autoComplete="current-password"
            />
            <button
              type="button"
              className={styles.togglePasswordBtn}
              onClick={() => setShowPassword(!showPassword)}
            >
              {showPassword ? <EyeOff /> : <Eye />}
            </button>
          </div>
          {fieldErrors.password && (
            <p className={styles.fieldError}>{fieldErrors.password}</p>
          )}
        </div>
        <div className={styles.errorBox}>
          {generalError && <div>{generalError}</div>}
        </div>
        <div>
          <button type="submit" disabled={isSubmitting}>
            {isSubmitting ? 'Logging in...' : 'Log In'}
          </button>
        </div>
      </form>
    </div>
  );
}
