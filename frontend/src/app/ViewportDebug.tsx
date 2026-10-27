'use client';

import { useEffect, useState } from 'react';

// TEMPORARY: diagnoses the tab bar gap in the iOS home screen app, remove afterwards
const PROBES = ['100vh', '100svh', '100dvh', '100lvh', 'env(safe-area-inset-top)', 'env(safe-area-inset-bottom)'];

export default function ViewportDebug() {
  const [lines, setLines] = useState<string[]>([]);

  useEffect(() => {
    const start = performance.now();
    const measure = () => {
      const probe = document.createElement('div');
      probe.style.position = 'absolute';
      probe.style.visibility = 'hidden';
      document.body.appendChild(probe);
      const probed = PROBES.map((value) => {
        probe.style.height = value;
        return `${value}: ${probe.offsetHeight}`;
      });
      probe.remove();

      const nav = document.querySelector('nav[aria-label="Main"]');
      setLines([
        `t: ${Math.round(performance.now() - start)}ms`,
        `standalone: ${String((navigator as { standalone?: boolean }).standalone)}`,
        `screen: ${screen.height}`,
        `innerHeight: ${innerHeight}`,
        `visualViewport: ${visualViewport?.height}`,
        `html clientHeight: ${document.documentElement.clientHeight}`,
        `body offsetHeight: ${document.body.offsetHeight}`,
        `scrollHeight: ${document.documentElement.scrollHeight}`,
        `tabbar bottom: ${nav ? Math.round(nav.getBoundingClientRect().bottom) : '-'}`,
        ...probed,
      ]);
    };

    measure();
    const interval = setInterval(measure, 500);
    return () => clearInterval(interval);
  }, []);

  return (
    <pre className="pointer-events-none fixed top-[calc(env(safe-area-inset-top)+60px)] left-2 z-[60] rounded bg-black/80 p-2 font-mono text-[11px] leading-tight text-white">
      {lines.join('\n')}
    </pre>
  );
}
