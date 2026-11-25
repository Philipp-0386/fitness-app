import type { Metadata } from 'next';
import { getTranslations } from 'next-intl/server';

import { AboutContent } from '@/features/about';

export async function generateMetadata(): Promise<Metadata> {
  const t = await getTranslations('about');
  return { title: t('metaTitle') };
}

export default function AboutPage() {
  return <AboutContent />;
}
