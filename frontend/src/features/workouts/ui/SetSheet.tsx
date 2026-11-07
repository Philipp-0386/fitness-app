'use client';

import { Trash2 } from 'lucide-react';
import { useEffect, useRef } from 'react';

import { Button } from '@/shared/ui/button';
import { Sheet } from '@/shared/ui/sheet';

type SetSheetProps = {
  /** "Set 2", "Warm-Up" or "Drop Set". */
  title: string;
  onRemove: () => void;
  onClose: () => void;
};

//options of one set; the caller mounts it while open
export default function SetSheet({ title, onRemove, onClose }: SetSheetProps) {
  const dialogRef = useRef<HTMLDialogElement>(null);

  useEffect(() => {
    dialogRef.current?.showModal();
  }, []);

  return (
    <Sheet ref={dialogRef} onClose={onClose} aria-labelledby="set-sheet-title">
      <div className="flex items-center justify-between gap-3">
        <h2 id="set-sheet-title" className="text-title">
          {title}
        </h2>
        <Button
          type="button"
          variant="text"
          size="sm"
          onClick={() => dialogRef.current?.close()}
        >
          Close
        </Button>
      </div>

      <div className="mt-4 border-t border-field-on-overlay pt-2">
        <Button
          type="button"
          variant="text"
          className="w-full text-danger hover:bg-field-on-overlay active:bg-field-on-overlay"
          onClick={onRemove}
        >
          <Trash2 aria-hidden="true" />
          Remove Set
        </Button>
      </div>
    </Sheet>
  );
}
