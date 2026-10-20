import { ChartColumn, ClipboardList, History, House } from 'lucide-react';

export const TABS = [
  { label: 'Today', href: '/dashboard', icon: House, match: ['/dashboard'] },
  { label: 'History', href: '/history', icon: History, match: ['/history'] },
  {
    label: 'Plans',
    href: '/routines',
    icon: ClipboardList,
    match: ['/routines', '/exercises'],
  },
  { label: 'Progress', href: '/progress', icon: ChartColumn, match: ['/progress'] },
];
