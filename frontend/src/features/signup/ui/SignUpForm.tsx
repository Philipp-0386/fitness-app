'use client';

import { useState } from 'react';
import { SignUpFormValues } from '../types/form.types';
import { signUp } from '../client/sign-up.client';
import { mapFormToPayload } from '../mapper/form-to-request.mapper';
import { ApiError } from '@/shared/api/errors/api-error';
import { evaluateApiError } from './api-error-evaluate';
import { SuccessfulSignUpResponse } from '../types/api.types';
import { useRouter } from 'next/navigation';
import { toast } from 'sonner';
import { CircleAlert, Eye, EyeOff } from 'lucide-react';
import { Button } from '@/shared/ui/button';
import { Input } from '@/shared/ui/input';
import { Label } from '@/shared/ui/label';

type FormError = Partial<Record<keyof SignUpFormValues, string>>;

type Touched = Partial<Record<keyof SignUpFormValues, boolean>>;

export default function UserForm() {
  const [form, setForm] = useState<SignUpFormValues>({
    username: '',
    email: '',
    password: '',
    confirmPassword: '',
  });
  const [generalErrors, setGeneralErrors] = useState<string | null>(null);
  const [fieldErrors, setFieldErrors] = useState<FormError>({});
  const [touched, setTouched] = useState<Touched>({});
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const router = useRouter();

  function handleChange(e: React.ChangeEvent<HTMLInputElement>) {
    const { name, value } = e.target;
    const fieldName = name as keyof SignUpFormValues;

    const updatedForm = {
      ...form,
      [fieldName]: value,
    };

    setForm(updatedForm);

    if (touched[fieldName]) {
      const error = validateField(fieldName, value, updatedForm);

      setFieldErrors((prev) => ({
        ...prev,
        [fieldName]: error,
      }));
    }
  }

  function validateField(
    name: keyof SignUpFormValues,
    value: string,
    currentForm: SignUpFormValues,
  ): string | undefined {
    switch (name) {
      case 'username':
        if (!value.trim()) return 'Enter a username';
        if (value.length < 5 || value.length > 32) return 'Use 5 to 32 characters';
        return undefined;
      case 'email':
        if (!value.trim()) return 'Enter your email address';
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(value)) return 'Enter a valid email address';
        return undefined;
      case 'password':
        if (!value.trim()) return 'Enter a password';
        if (value.trim().length < 8) return 'Use at least 8 characters';
        if (!/[A-Z]/.test(value) || !/[a-z]/.test(value) || !/\d/.test(value))
          return 'Include an uppercase letter, a lowercase letter and a number';
        return undefined;
      case 'confirmPassword':
        if (!value.trim()) return 'Repeat your password';
        if (value !== currentForm.password) return "Passwords don't match";
        return undefined;
    }
    return undefined;
  }

  function handleBlur(e: React.FocusEvent<HTMLInputElement>) {
    const { name, value } = e.target;
    const fieldName = name as keyof SignUpFormValues;

    setTouched((prev) => ({
      ...prev,
      [fieldName]: true,
    }));

    const error = validateField(fieldName, value, form);

    setFieldErrors((prev) => ({
      ...prev,
      [fieldName]: error,
    }));
  }

  function validateFinal(currentForm: SignUpFormValues = form): FormError {
    const newErrors: FormError = {};

    (Object.keys(currentForm) as (keyof SignUpFormValues)[]).forEach((field) => {
      const error = validateField(field, currentForm[field], currentForm);
      if (error) {
        newErrors[field] = error;
      }
    });

    return newErrors;
  }

  async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    if (isSubmitting) return;
    setFieldErrors({});
    setGeneralErrors(null);

    const allTouched: Touched = {
      username: true,
      email: true,
      password: true,
      confirmPassword: true,
    };
    setTouched(allTouched);

    const validationErrors = validateFinal(form);
    if (Object.keys(validationErrors).length > 0) {
      setFieldErrors(validationErrors);
      return;
    }

    setIsSubmitting(true);
    const request = mapFormToPayload(form);
    let result: SuccessfulSignUpResponse | null = null;
    try {
      result = await signUp(request);

      setFieldErrors({});
      setGeneralErrors(null);

      toast.success(
        `User ${result.username} with email ${result.email} created successfully!`,
      );

      setTimeout(() => {
        router.push('/');
      }, 2500);
    } catch (error) {
      // Only re-enable on failure; on success the button stays locked until the redirect.
      setIsSubmitting(false);

      if (error instanceof ApiError) {
        const uiError = evaluateApiError(error);
        setGeneralErrors(uiError.generalError);
        setFieldErrors(uiError.fieldErrors);
        return;
      }

      setGeneralErrors('An unexpected error occurred. Please try again later.');
    }
  }

  return (
    <form onSubmit={handleSubmit} noValidate>
      <div className="mb-4">
        <Label className="mb-1.5" htmlFor="username">
          Username
        </Label>
        <Input
          name="username"
          id="username"
          value={form.username}
          onChange={handleChange}
          onBlur={handleBlur}
          autoComplete="username"
          aria-invalid={!!(touched.username && fieldErrors.username)}
          aria-describedby={
            touched.username && fieldErrors.username ? 'username-error' : undefined
          }
        />
        {touched.username && fieldErrors.username && (
          <p
            id="username-error"
            className="mt-1 flex items-center gap-1.5 text-meta text-danger"
          >
            <CircleAlert className="size-4 shrink-0" aria-hidden="true" />
            {fieldErrors.username}
          </p>
        )}
      </div>
      <div className="mb-4">
        <Label className="mb-1.5" htmlFor="email">
          Email
        </Label>
        <Input
          type="email"
          name="email"
          id="email"
          value={form.email}
          onChange={handleChange}
          onBlur={handleBlur}
          autoComplete="email"
          aria-invalid={!!(touched.email && fieldErrors.email)}
          aria-describedby={
            touched.email && fieldErrors.email ? 'email-error' : undefined
          }
        />
        {touched.email && fieldErrors.email && (
          <p
            id="email-error"
            className="mt-1 flex items-center gap-1.5 text-meta text-danger"
          >
            <CircleAlert className="size-4 shrink-0" aria-hidden="true" />
            {fieldErrors.email}
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
            onBlur={handleBlur}
            autoComplete="new-password"
            aria-invalid={!!(touched.password && fieldErrors.password)}
            aria-describedby={
              touched.password && fieldErrors.password ? 'password-error' : undefined
            }
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
        {touched.password && fieldErrors.password && (
          <p
            id="password-error"
            className="mt-1 flex items-center gap-1.5 text-meta text-danger"
          >
            <CircleAlert className="size-4 shrink-0" aria-hidden="true" />
            {fieldErrors.password}
          </p>
        )}
      </div>
      <div className="mb-4">
        <Label className="mb-1.5" htmlFor="confirmPassword">
          Confirm password
        </Label>
        <div className="relative">
          <Input
            className="pr-13"
            type={showConfirmPassword ? 'text' : 'password'}
            name="confirmPassword"
            id="confirmPassword"
            value={form.confirmPassword}
            onChange={handleChange}
            onBlur={handleBlur}
            autoComplete="new-password"
            aria-invalid={!!(touched.confirmPassword && fieldErrors.confirmPassword)}
            aria-describedby={
              touched.confirmPassword && fieldErrors.confirmPassword
                ? 'confirmPassword-error'
                : undefined
            }
          />
          <Button
            className="absolute top-0.5 right-0.5"
            type="button"
            size="icon"
            variant="text"
            aria-label={showConfirmPassword ? 'Hide confirmation' : 'Show confirmation'}
            aria-pressed={showConfirmPassword}
            onClick={() => setShowConfirmPassword(!showConfirmPassword)}
          >
            {showConfirmPassword ? <EyeOff /> : <Eye />}
          </Button>
        </div>
        {touched.confirmPassword && fieldErrors.confirmPassword && (
          <p
            id="confirmPassword-error"
            className="mt-1 flex items-center gap-1.5 text-meta text-danger"
          >
            <CircleAlert className="size-4 shrink-0" aria-hidden="true" />
            {fieldErrors.confirmPassword}
          </p>
        )}
      </div>
      {generalErrors && (
        <div
          role="alert"
          className="mb-2 flex items-start gap-2.5 rounded-field bg-danger-subtle p-3 text-meta"
        >
          <CircleAlert className="size-5 shrink-0 text-danger" aria-hidden="true" />
          <span>{generalErrors}</span>
        </div>
      )}
      <Button
        className="mt-2"
        type="submit"
        variant="primary"
        size="lg"
        disabled={isSubmitting}
      >
        {isSubmitting ? 'Creating account...' : 'Create account'}
      </Button>
    </form>
  );
}
