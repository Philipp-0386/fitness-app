import { ChartColumn, ClipboardList, History, House } from 'lucide-react';

export const TABS = [
  { labelKey: 'today', href: '/dashboard', icon: House, match: ['/dashboard'] },
  { labelKey: 'history', href: '/history', icon: History, match: ['/history'] },
  {
    labelKey: 'plans',
    href: '/plans',
    icon: ClipboardList,
    match: ['/plans', '/exercises'],
  },
  { labelKey: 'progress', href: '/progress', icon: ChartColumn, match: ['/progress'] },
] as const;
