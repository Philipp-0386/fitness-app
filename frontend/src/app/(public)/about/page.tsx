import type { Metadata } from 'next';

import { AboutContent } from '@/features/about';

export const metadata: Metadata = {
  title: 'About | Fitness App',
};

export default function AboutPage() {
  return <AboutContent />;
}
