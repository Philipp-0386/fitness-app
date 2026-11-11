'use client';

import { useState, useSyncExternalStore } from 'react';
import { toast } from 'sonner';

import { fallbackErrorMessage } from '@/shared/api/errors/fallback-error-message';
import { cn } from '@/shared/lib/utils';
import { Button } from '@/shared/ui/button';
import { listClass } from '@/shared/ui/list';
import { updateSettings } from '../client/update-settings.client';
import { UserSettings } from '../types/api.types';

const weightOptions = [
  { value: 'KG', label: 'kg' },
  { value: 'LB', label: 'lb' },
] as const;

const distanceOptions = [
  { value: 'KM', label: 'km' },
  { value: 'MI', label: 'mi' },
] as const;

const noSubscription = () => () => {};
const readDeviceTimeZone = () => Intl.DateTimeFormat().resolvedOptions().timeZone;
const noDeviceTimeZoneOnServer = () => null;

type Props = {
  initialSettings: UserSettings;
};

export default function UnitSettings({ initialSettings }: Props) {
  const [settings, setSettings] = useState(initialSettings);
  const [isSaving, setIsSaving] = useState(false);
  const deviceTimeZone = useSyncExternalStore(
    noSubscription,
    readDeviceTimeZone,
    noDeviceTimeZoneOnServer,
  );
  const timeZoneDiffers = deviceTimeZone !== null && deviceTimeZone !== settings.timeZone;

  async function handleChange(next: UserSettings) {
    const previous = settings;
    setSettings(next);
    setIsSaving(true);
    try {
      setSettings(await updateSettings(next));
    } catch (err) {
      setSettings(previous);
      toast.error(fallbackErrorMessage(err));
    } finally {
      setIsSaving(false);
    }
  }

  return (
    <section aria-labelledby="preferences-title" className="mx-4 mt-8">
      <h2 id="preferences-title" className="mb-3 text-title">
        Preferences
      </h2>
      <ul className={listClass}>
        <li className="flex min-h-14 items-center gap-3 px-4 py-2.5">
          <span className="min-w-0 flex-1 font-semibold">Weight</span>
          <div
            role="radiogroup"
            aria-label="Weight unit"
            className="inline-grid grid-flow-col gap-1 rounded-card bg-bg p-1"
          >
            {weightOptions.map(({ value, label }) => (
              <button
                key={value}
                type="button"
                role="radio"
                aria-checked={settings.weightUnit === value}
                disabled={isSaving}
                onClick={() => handleChange({ ...settings, weightUnit: value })}
                className={cn(
                  'min-h-9 rounded-field px-3.5 text-meta font-semibold',
                  settings.weightUnit === value
                    ? 'bg-surface-raised text-text-primary'
                    : 'text-text-tertiary hover:text-text-secondary',
                )}
              >
                {label}
              </button>
            ))}
          </div>
        </li>
        <li className="flex min-h-14 items-center gap-3 px-4 py-2.5">
          <span className="min-w-0 flex-1 font-semibold">Distance</span>
          <div
            role="radiogroup"
            aria-label="Distance unit"
            className="inline-grid grid-flow-col gap-1 rounded-card bg-bg p-1"
          >
            {distanceOptions.map(({ value, label }) => (
              <button
                key={value}
                type="button"
                role="radio"
                aria-checked={settings.distanceUnit === value}
                disabled={isSaving}
                onClick={() => handleChange({ ...settings, distanceUnit: value })}
                className={cn(
                  'min-h-9 rounded-field px-3.5 text-meta font-semibold',
                  settings.distanceUnit === value
                    ? 'bg-surface-raised text-text-primary'
                    : 'text-text-tertiary hover:text-text-secondary',
                )}
              >
                {label}
              </button>
            ))}
          </div>
        </li>
        <li className="px-4 py-2.5">
          <div className="flex min-h-9 items-center gap-3">
            <span className="min-w-0 flex-1 font-semibold">Time Zone</span>
            <span className="truncate text-meta text-text-secondary">
              {settings.timeZone.replaceAll('_', ' ')}
            </span>
          </div>
          {timeZoneDiffers && (
            <div className="mt-2 flex items-center gap-3">
              <p className="min-w-0 flex-1 text-meta text-text-tertiary">
                Your device uses {deviceTimeZone.replaceAll('_', ' ')}.
              </p>
              <Button
                type="button"
                size="sm"
                disabled={isSaving}
                onClick={() => handleChange({ ...settings, timeZone: deviceTimeZone })}
              >
                Use Device Time Zone
              </Button>
            </div>
          )}
        </li>
      </ul>
    </section>
  );
}
