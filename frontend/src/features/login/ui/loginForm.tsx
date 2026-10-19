'use client';

import { useState } from 'react';
import { login } from '../client/login.client';
import { ApiError } from '@/shared/api/errors/api-error';
import { fallbackErrorMessage } from '@/shared/api/errors/fallback-error-message';
import { useRouter } from 'next/navigation';
import { CircleAlert, Eye, EyeOff } from 'lucide-react';
import { Button } from '@/shared/ui/button';
import { Input } from '@/shared/ui/input';
import { Label } from '@/shared/ui/label';

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
    e.preventDefault();
    if (isSubmitting) return;
    setGeneralError(null);
    setFieldErrors({});

    const errors: FormError = {};
    if (!form.username.trim()) errors.username = 'Enter your username';
    if (!form.password.trim()) errors.password = 'Enter your password';
    if (Object.keys(errors).length > 0) {
      setFieldErrors(errors);
      return;
    }

    setIsSubmitting(true);
    try {
      await login({ username: form.username, password: form.password });
      //Replace instead of push: the login form must not stay in the history stack.
      router.replace('/dashboard');
      router.refresh();
    } catch (error) {
      //401 means wrong credentials, not an expired session
      if (error instanceof ApiError && error.code === 'INVALID_CREDENTIALS') {
        setGeneralError('Wrong username or password. Check both and try again.');
        return;
      }
      setGeneralError(fallbackErrorMessage(error));
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <form onSubmit={handleSubmit} noValidate>
      <div className="mb-4">
        <Label className="mb-1.5" htmlFor="username">
          Username
        </Label>
        <Input
          type="text"
          name="username"
          id="username"
          value={form.username}
          onChange={handleChange}
          autoComplete="username"
          aria-invalid={!!fieldErrors.username}
          aria-describedby={fieldErrors.username ? 'username-error' : undefined}
        />
        {fieldErrors.username && (
          <p
            id="username-error"
            className="mt-1 flex items-center gap-1.5 text-meta text-danger"
          >
            <CircleAlert className="size-4" aria-hidden="true" />
            {fieldErrors.username}
          </p>
        )}
      </div>
      <div className="mb-4">
        <Label className="mb-1.5" htmlFor="password">
          Password
        </Label>
        <div className="relative">
          <Input
            className="pr-13"
            type={showPassword ? 'text' : 'password'}
            name="password"
            id="password"
            value={form.password}
            onChange={handleChange}
            autoComplete="current-password"
            aria-invalid={!!fieldErrors.password}
            aria-describedby={fieldErrors.password ? 'password-error' : undefined}
          />
          <Button
            className="absolute top-0.5 right-0.5"
            type="button"
            size="icon"
            variant="text"
            aria-label={showPassword ? 'Hide password' : 'Show password'}
            aria-pressed={showPassword}
            onClick={() => setShowPassword(!showPassword)}
          >
            {showPassword ? <EyeOff /> : <Eye />}
          </Button>
        </div>
        {fieldErrors.password && (
          <p
            id="password-error"
            className="mt-1 flex items-center gap-1.5 text-meta text-danger"
          >
            <CircleAlert className="size-4" aria-hidden="true" />
            {fieldErrors.password}
          </p>
        )}
      </div>
      {generalError && (
        <div
          role="alert"
          className="mb-2 flex items-start gap-2.5 rounded-field bg-danger-subtle p-3 text-meta"
        >
          <CircleAlert className="size-5 shrink-0 text-danger" aria-hidden="true" />
          <span>{generalError}</span>
        </div>
      )}
      <Button
        className="mt-2"
        type="submit"
        variant="primary"
        size="lg"
        disabled={isSubmitting}
      >
        {isSubmitting ? 'Logging in...' : 'Log in'}
      </Button>
    </form>
  );
}
