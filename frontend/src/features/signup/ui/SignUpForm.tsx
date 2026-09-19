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
import { Eye, EyeOff } from 'lucide-react';

import styles from './signUpForm.module.css';

type FormError = Partial<Record<keyof SignUpFormValues, string>>;

type Touched = Partial<Record<keyof SignUpFormValues, boolean>>;

export default function UserForm() {
  const [form, setForm] = useState<SignUpFormValues>({
    username: '',
    email: '',
    password: '',
    confirmPassword: '',
    firstName: '',
    lastName: '',
    dateOfBirth: '',
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
        if (!value.trim()) return 'Username is required';
        if (value.length < 5) return 'Username must be at least 5 characters long';
        return undefined;
      case 'email':
        if (!value.trim()) return 'Email is required';
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(value)) return 'Please enter a valid email address';
        return undefined;
      case 'password':
        if (!value.trim()) return 'Password is required';
        if (value.trim().length < 8) return 'Password must be at least 8 characters long';
        if (!/[A-Z]/.test(value))
          return 'Password must contain at least one uppercase letter';
        if (!/[a-z]/.test(value))
          return 'Password must contain at least one lowercase letter';
        if (!/\d/.test(value)) return 'Password must contain at least one number';
        return undefined;
      case 'confirmPassword':
        if (!value.trim()) return 'Please confirm your password';
        if (value !== currentForm.password) return 'Passwords do not match';
        return undefined;
      case 'firstName':
        if (!value.trim()) return 'First name is required';
        return undefined;
      case 'lastName':
        if (!value.trim()) return 'Last name is required';
        return undefined;
      case 'dateOfBirth':
        if (!value.trim()) return 'Date of birth is required';
        const date = new Date(value);
        if (isNaN(date.getTime())) return 'Date of birth must be a valid date';
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
      firstName: true,
      lastName: true,
      dateOfBirth: true,
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
    <div>
      <form onSubmit={handleSubmit} noValidate className={styles.form}>
        <div className={styles.inputBlock}>
          <label>Username:</label>
          <input
            type="text"
            name="username"
            value={form.username}
            onChange={handleChange}
            onBlur={handleBlur}
          />
          {touched.username && fieldErrors.username && (
            <p className={styles.fieldError}>{fieldErrors.username}</p>
          )}
        </div>
        <div className={styles.inputBlock}>
          <label>Email:</label>
          <input
            type="email"
            name="email"
            value={form.email}
            onChange={handleChange}
            onBlur={handleBlur}
          />
          {touched.email && fieldErrors.email && (
            <p className={styles.fieldError}>{fieldErrors.email}</p>
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
              onBlur={handleBlur}
            />
            <button
              type="button"
              className={styles.togglePasswordBtn}
              onClick={() => setShowPassword(!showPassword)}
            >
              {showPassword ? <EyeOff /> : <Eye />}
            </button>
          </div>
          {touched.password && fieldErrors.password && (
            <p className={styles.fieldError}>{fieldErrors.password}</p>
          )}
        </div>
        <div className={styles.inputBlock}>
          <label>Confirm Password:</label>
          <div className={styles.passwordInputWrapper}>
            <input
              type={showConfirmPassword ? 'text' : 'password'}
              name="confirmPassword"
              value={form.confirmPassword}
              onChange={handleChange}
              onBlur={handleBlur}
            />
            <button
              type="button"
              className={styles.togglePasswordBtn}
              onClick={() => setShowConfirmPassword(!showConfirmPassword)}
            >
              {showConfirmPassword ? <EyeOff /> : <Eye />}
            </button>
          </div>
          {touched.confirmPassword && fieldErrors.confirmPassword && (
            <p className={styles.fieldError}>{fieldErrors.confirmPassword}</p>
          )}
        </div>
        <div className={styles.inputBlock}>
          <label>First Name:</label>
          <input
            type="text"
            name="firstName"
            value={form.firstName}
            onChange={handleChange}
            onBlur={handleBlur}
          />
          {touched.firstName && fieldErrors.firstName && (
            <p className={styles.fieldError}>{fieldErrors.firstName}</p>
          )}
        </div>
        <div className={styles.inputBlock}>
          <label>Last Name:</label>
          <input
            type="text"
            name="lastName"
            value={form.lastName}
            onChange={handleChange}
            onBlur={handleBlur}
          />
          {touched.lastName && fieldErrors.lastName && (
            <p className={styles.fieldError}>{fieldErrors.lastName}</p>
          )}
        </div>
        <div className={styles.inputBlock}>
          <label>Date of Birth:</label>
          <input
            type="date"
            name="dateOfBirth"
            value={form.dateOfBirth}
            onChange={handleChange}
            onBlur={handleBlur}
          />
          {touched.dateOfBirth && fieldErrors.dateOfBirth && (
            <p className={styles.fieldError}>{fieldErrors.dateOfBirth}</p>
          )}
        </div>
        <div className={styles.errorBox}>
          {generalErrors && <div>{generalErrors}</div>}
        </div>
        <div>
          <button type="submit" disabled={isSubmitting}>
            {isSubmitting ? 'Submitting...' : 'Submit'}
          </button>
        </div>
      </form>
    </div>
  );
}
