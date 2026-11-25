import type { Metadata } from 'next';

import { PrivacyPolicy } from '@/features/legal';

export const metadata: Metadata = {
  title: 'Datenschutz | Avren',
};

export default function PrivacyPage() {
  return <PrivacyPolicy />;
}
