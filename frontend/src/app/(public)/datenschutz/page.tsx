import type { Metadata } from 'next';

import { PrivacyPolicy } from '@/features/legal';

export const metadata: Metadata = {
  title: 'Datenschutz | Fitness App',
};

export default function PrivacyPage() {
  return <PrivacyPolicy />;
}
