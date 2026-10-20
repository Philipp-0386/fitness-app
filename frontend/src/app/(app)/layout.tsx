import { fetchMe } from '@/features/user';
import { SideBar, SiteFooter, TabBar, WipBanner } from '@/shared/layout';
import { cookies } from 'next/headers';

export default async function AppLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  const username = await fetchMe()
    .then((user) => user.username)
    .catch(() => null);
  const wipBannerHidden = (await cookies()).has('wip_hidden');

  return (
    <>
      {!wipBannerHidden && <WipBanner dismissible />}
      <div className="flex flex-1 flex-col lg:flex-row">
        <SideBar username={username} />
        <div className="flex min-w-0 flex-1 flex-col">
          <div className="flex flex-1 flex-col">{children}</div>
          <div className="hidden lg:block">
            <SiteFooter />
          </div>
        </div>
      </div>
      <TabBar />
    </>
  );
}
