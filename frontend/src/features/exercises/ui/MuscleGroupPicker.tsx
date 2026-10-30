'use client';

import { Check, ChevronLeft, ChevronRight, Plus } from 'lucide-react';
import { useId, useRef, useState } from 'react';

import { Button } from '@/shared/ui/button';
import { Sheet } from '@/shared/ui/sheet';
import { BodyRegion, MuscleGroup } from '../types/api.types';

const regions: { value: BodyRegion; label: string }[] = [
  { value: 'UPPER', label: 'Upper body' },
  { value: 'CORE', label: 'Core' },
  { value: 'LOWER', label: 'Lower body' },
];

const optionClass =
  'flex min-h-12 flex-1 items-center gap-2.5 px-1 text-left font-semibold text-text-secondary disabled:text-text-tertiary';

type MuscleGroupPickerProps = {
  muscleGroups: MuscleGroup[];
  /** Sheet title, e.g. Primary Muscle. */
  title: string;
  /** Groups and areas already on the exercise, shown checked and not pickable. */
  pickedIds: number[];
  onPick: (muscle: { id: number; name: string }) => void;
};

//add chip that opens a sheet with two levels like the muscle group filter, a tap picks one group or area and closes it
export default function MuscleGroupPicker({
  muscleGroups,
  title,
  pickedIds,
  onPick,
}: MuscleGroupPickerProps) {
  const dialogRef = useRef<HTMLDialogElement>(null);
  const id = useId();
  const [openGroupId, setOpenGroupId] = useState<number | null>(null);

  const openGroup = muscleGroups.find((group) => group.id === openGroupId);

  function pick(muscleId: number, name: string) {
    onPick({ id: muscleId, name });
    dialogRef.current?.close();
  }

  return (
    <>
      <button
        type="button"
        aria-haspopup="dialog"
        aria-label={`Add ${title.toLowerCase()}`}
        onClick={() => {
          setOpenGroupId(null);
          dialogRef.current?.showModal();
        }}
        className="relative inline-flex h-8.5 items-center gap-1 rounded-full border border-dashed border-border-strong px-3 text-meta font-semibold text-text-secondary before:absolute before:inset-x-0 before:-inset-y-[5px] before:content-['']"
      >
        <Plus className="size-4" aria-hidden="true" />
        Add
      </button>

      {/* React passes close and cancel on to parent handlers, which would also close a sheet this one sits in. */}
      <Sheet
        ref={dialogRef}
        onClose={(e) => e.stopPropagation()}
        onCancel={(e) => e.stopPropagation()}
        aria-labelledby={`${id}-title`}
      >
        {openGroup ? (
          <>
            <button
              type="button"
              onClick={() => setOpenGroupId(null)}
              className="-ml-2 flex min-h-tap items-center gap-0.5 pr-2 font-semibold text-text-secondary hover:text-text-primary"
            >
              <ChevronLeft className="size-5" aria-hidden="true" />
              {title}
            </button>
            <h2 id={`${id}-title`} className="text-title">
              {openGroup.name}
            </h2>
            <ul className="mt-2 divide-y divide-field-on-overlay">
              {[
                { id: openGroup.id, name: `All of ${openGroup.name}` },
                ...openGroup.areas,
              ].map((area) => {
                const picked = pickedIds.includes(area.id);
                return (
                  <li key={area.id} className="flex">
                    <button
                      type="button"
                      disabled={picked}
                      onClick={() =>
                        pick(
                          area.id,
                          area.id === openGroup.id ? openGroup.name : area.name,
                        )
                      }
                      className={optionClass}
                    >
                      {picked ? (
                        <Check className="size-5 shrink-0" aria-hidden="true" />
                      ) : (
                        <span className="w-5 shrink-0" />
                      )}
                      {area.name}
                    </button>
                  </li>
                );
              })}
            </ul>
          </>
        ) : (
          <>
            <div className="flex items-start justify-between gap-3">
              <h2 id={`${id}-title`} className="text-title">
                {title}
              </h2>
              <Button
                type="button"
                variant="text"
                size="sm"
                onClick={() => dialogRef.current?.close()}
              >
                Cancel
              </Button>
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
                  aria-labelledby={`${id}-region-${region.value}`}
                >
                  <h3
                    id={`${id}-region-${region.value}`}
                    className="mb-1 text-meta font-semibold text-text-tertiary"
                  >
                    {region.label}
                  </h3>
                  <ul className="divide-y divide-field-on-overlay">
                    {groups.map((group) => {
                      const picked = pickedIds.includes(group.id);
                      return (
                        <li key={group.id} className="flex items-center">
                          <button
                            type="button"
                            disabled={picked}
                            onClick={() => pick(group.id, group.name)}
                            className={optionClass}
                          >
                            {picked ? (
                              <Check className="size-5 shrink-0" aria-hidden="true" />
                            ) : (
                              <span className="w-5 shrink-0" />
                            )}
                            {group.name}
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
