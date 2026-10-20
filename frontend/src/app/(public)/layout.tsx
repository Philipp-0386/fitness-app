import { SiteFooter } from '@/shared/layout';

export default function PublicLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <>
      <div className="flex flex-1 flex-col">{children}</div>
      <SiteFooter />
    </>
  );
}
