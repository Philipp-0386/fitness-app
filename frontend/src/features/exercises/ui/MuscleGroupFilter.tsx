'use client';

import { Check, ChevronDown, ChevronLeft, ChevronRight, X } from 'lucide-react';
import { useRef, useState } from 'react';

import { cn } from '@/shared/lib/utils';
import { Button } from '@/shared/ui/button';
import { Sheet } from '@/shared/ui/sheet';
import { MuscleFilter } from '../lib/muscle-filter';
import { BodyRegion, MuscleGroup } from '../types/api.types';

const regions: { value: BodyRegion; label: string }[] = [
  { value: 'UPPER', label: 'Upper body' },
  { value: 'CORE', label: 'Core' },
  { value: 'LOWER', label: 'Lower body' },
];

const roleOptions = [
  { value: 'PRIMARY', label: 'Primary' },
  { value: 'SECONDARY', label: 'Secondary' },
  { value: 'EITHER', label: 'Either' },
] as const;

const chipClass =
  'relative inline-flex h-8.5 shrink-0 items-center gap-1.5 rounded-full border text-meta font-semibold whitespace-nowrap';

// before: extends the 34px chip to a 44px hit area
const hitAreaClass =
  "before:absolute before:inset-x-0 before:-inset-y-[5px] before:content-['']";

const optionClass =
  'flex min-h-12 flex-1 items-center gap-2.5 px-1 text-left font-semibold aria-checked:text-text-primary';

type MuscleGroupFilterProps = {
  muscleGroups: MuscleGroup[];
  value: MuscleFilter | null;
  onChange: (value: MuscleFilter | null) => void;
};

//chip that opens a sheet with two levels: the groups by body region, then the areas of one group
export default function MuscleGroupFilter({
  muscleGroups,
  value,
  onChange,
}: MuscleGroupFilterProps) {
  const dialogRef = useRef<HTMLDialogElement>(null);
  const [role, setRole] = useState<MuscleFilter['role']>('PRIMARY');
  const [openGroupId, setOpenGroupId] = useState<number | null>(null);

  const openGroup = muscleGroups.find((group) => group.id === openGroupId);

  function open() {
    setRole(value?.role ?? 'PRIMARY');
    setOpenGroupId(null);
    dialogRef.current?.showModal();
  }

  function pick(groupId: number, areaId: number | null, label: string) {
    onChange({ groupId, areaId, label, role });
    dialogRef.current?.close();
  }

  function changeRole(next: MuscleFilter['role']) {
    setRole(next);
    if (value) onChange({ ...value, role: next });
  }

  return (
    <>
      {value ? (
        <span
          className={cn(
            chipClass,
            'border-text-primary bg-surface-overlay text-text-primary',
          )}
        >
          <button
            type="button"
            aria-haspopup="dialog"
            aria-label={`Muscle group: ${value.label}. Change`}
            onClick={open}
            className={cn(
              'relative inline-flex h-full items-center gap-1.5 rounded-l-full pr-1 pl-3',
              hitAreaClass,
            )}
          >
            <Check className="size-4 shrink-0" aria-hidden="true" />
            <span className="max-w-40 truncate">
              {value.label}
              {value.role === 'SECONDARY' && ', secondary'}
              {value.role === 'EITHER' && ', either'}
            </span>
          </button>
          <button
            type="button"
            aria-label="Clear muscle group"
            onClick={() => onChange(null)}
            className={cn(
              'relative inline-flex h-full items-center rounded-r-full pr-2.5 pl-1',
              hitAreaClass,
            )}
          >
            <X className="size-4" aria-hidden="true" />
          </button>
        </span>
      ) : (
        <button
          type="button"
          aria-haspopup="dialog"
          onClick={open}
          className={cn(
            chipClass,
            'border-border-strong px-3 text-text-secondary',
            hitAreaClass,
          )}
        >
          Muscle Group
          <ChevronDown className="size-4" aria-hidden="true" />
        </button>
      )}

      {/* React passes close and cancel on to parent handlers, which would also close a sheet this one sits in. */}
      <Sheet
        ref={dialogRef}
        onClose={(e) => e.stopPropagation()}
        onCancel={(e) => e.stopPropagation()}
        aria-labelledby="muscle-group-title"
      >
        {openGroup ? (
          <>
            <button
              type="button"
              onClick={() => setOpenGroupId(null)}
              className="-ml-2 flex min-h-tap items-center gap-0.5 pr-2 font-semibold text-text-secondary hover:text-text-primary"
            >
              <ChevronLeft className="size-5" aria-hidden="true" />
              Muscle Group
            </button>
            <h2 id="muscle-group-title" className="text-title">
              {openGroup.name}
            </h2>
            <ul
              role="radiogroup"
              aria-label={`Area of ${openGroup.name}`}
              className="mt-2 divide-y divide-field-on-overlay"
            >
              {[{ id: null, name: `All of ${openGroup.name}` }, ...openGroup.areas].map(
                (area) => {
                  const checked =
                    value?.groupId === openGroup.id && value.areaId === area.id;
                  return (
                    <li key={area.id ?? 'all'} role="none" className="flex">
                      <button
                        type="button"
                        role="radio"
                        aria-checked={checked}
                        onClick={() =>
                          pick(
                            openGroup.id,
                            area.id,
                            area.id ? area.name : openGroup.name,
                          )
                        }
                        className={cn(optionClass, 'text-text-secondary')}
                      >
                        {checked ? (
                          <Check className="size-5 shrink-0" aria-hidden="true" />
                        ) : (
                          <span className="w-5 shrink-0" />
                        )}
                        {area.name}
                      </button>
                    </li>
                  );
                },
              )}
            </ul>
          </>
        ) : (
          <>
            <div className="flex items-start justify-between gap-3">
              <h2 id="muscle-group-title" className="text-title">
                Muscle Group
              </h2>
              <Button
                type="button"
                variant="text"
                size="sm"
                onClick={() => dialogRef.current?.close()}
              >
                Done
              </Button>
            </div>

            <p
              id="muscle-role-label"
              className="mt-3 mb-1.5 text-meta font-semibold text-text-secondary"
            >
              Trained as
            </p>
            <div
              role="radiogroup"
              aria-labelledby="muscle-role-label"
              className="inline-grid grid-flow-col gap-1 rounded-card bg-surface p-1"
            >
              {roleOptions.map((option) => (
                <button
                  key={option.value}
                  type="button"
                  role="radio"
                  aria-checked={role === option.value}
                  onClick={() => changeRole(option.value)}
                  className={cn(
                    'min-h-9 rounded-field px-3.5 text-meta font-semibold',
                    role === option.value
                      ? 'bg-field-on-overlay text-text-primary'
                      : 'text-text-tertiary hover:text-text-secondary',
                  )}
                >
                  {option.label}
                </button>
              ))}
            </div>

            {regions.map((region) => {
              const groups = muscleGroups.filter(
                (group) => group.bodyRegion === region.value,
              );
              if (groups.length === 0) return null;
              return (
                <section
                  key={region.value}
                  className="mt-4"
                  aria-labelledby={`region-${region.value}`}
                >
                  <h3
                    id={`region-${region.value}`}
                    className="mb-1 text-meta font-semibold text-text-tertiary"
                  >
                    {region.label}
                  </h3>
                  <ul
                    role="radiogroup"
                    aria-labelledby={`region-${region.value}`}
                    className="divide-y divide-field-on-overlay"
                  >
                    {groups.map((group) => {
                      const checked =
                        value?.groupId === group.id && value.areaId === null;
                      const pickedArea =
                        value?.groupId === group.id && value.areaId !== null
                          ? value.label
                          : null;
                      return (
                        <li key={group.id} role="none" className="flex items-center">
                          <button
                            type="button"
                            role="radio"
                            aria-checked={checked}
                            onClick={() => pick(group.id, null, group.name)}
                            className={cn(
                              optionClass,
                              pickedArea ? 'text-text-primary' : 'text-text-secondary',
                            )}
                          >
                            {checked ? (
                              <Check className="size-5 shrink-0" aria-hidden="true" />
                            ) : (
                              <span className="w-5 shrink-0" />
                            )}
                            {group.name}
                            {pickedArea && (
                              <span className="text-meta font-normal text-text-tertiary">
                                , {pickedArea}
                              </span>
                            )}
                          </button>
                          {group.areas.length > 0 && (
                            <button
                              type="button"
                              aria-label={`Areas of ${group.name}`}
                              onClick={() => setOpenGroupId(group.id)}
                              className="grid size-tap shrink-0 place-items-center rounded-card text-text-secondary active:bg-surface-raised"
                            >
                              <ChevronRight className="size-5" aria-hidden="true" />
                            </button>
                          )}
                        </li>
                      );
                    })}
                  </ul>
                </section>
              );
            })}
          </>
        )}
      </Sheet>
    </>
  );
}
