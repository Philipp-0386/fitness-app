import { ChartColumn, ClipboardList, History, House } from 'lucide-react';

export const TABS = [
  { label: 'Today', href: '/dashboard', icon: House, match: ['/dashboard'] },
  { label: 'History', href: '/history', icon: History, match: ['/history'] },
  {
    label: 'Plans',
    href: '/plans',
    icon: ClipboardList,
    match: ['/plans', '/exercises'],
  },
  { label: 'Progress', href: '/progress', icon: ChartColumn, match: ['/progress'] },
];
