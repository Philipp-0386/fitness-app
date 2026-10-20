import { LockKeyhole } from 'lucide-react';

export default function SignUpClosedBanner() {
  return (
    <aside
      aria-label="Sign-up status"
      className="mb-8 flex items-start gap-3 rounded-card bg-surface p-4 text-meta text-text-secondary"
    >
      <LockKeyhole className="mt-0.5 size-4 shrink-0" aria-hidden="true" />
      <p>
        <strong className="font-semibold text-text-primary">Sign-ups are closed.</strong>{' '}
        Accounts are created on invitation.
      </p>
    </aside>
  );
}
