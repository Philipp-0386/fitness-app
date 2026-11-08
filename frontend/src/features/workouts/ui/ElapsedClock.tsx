'use client';

import { useEffect, useState } from 'react';

type ElapsedClockProps = {
  startedAt: string;
};

export default function ElapsedClock({ startedAt }: ElapsedClockProps) {
  const [now, setNow] = useState(() => Date.now());

  useEffect(() => {
    const id = setInterval(() => setNow(Date.now()), 1000);
    return () => clearInterval(id);
  }, []);

  const total = Math.floor(Math.max(0, now - Date.parse(startedAt)) / 1000);
  const hours = Math.floor(total / 3600);
  const minutes = Math.floor((total % 3600) / 60);
  const seconds = String(total % 60).padStart(2, '0');
  const text =
    hours > 0
      ? `${hours}:${String(minutes).padStart(2, '0')}:${seconds}`
      : `${minutes}:${seconds}`;

  return (
    <span className="tabular-nums" suppressHydrationWarning>
      {text}
    </span>
  );
}
