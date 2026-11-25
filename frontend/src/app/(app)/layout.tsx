import { fetchMe } from '@/features/user';
import { SideBar, SiteFooter, TabBar } from '@/shared/layout';

export default async function AppLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  const username = await fetchMe()
    .then((user) => user.username)
    .catch(() => null);

  return (
    <>
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
