import Link from "next/link"

export default function Smoketest() {
  return (
    <div>
      <main >
        <h1>Home</h1>
        <ul>
          <li><Link href="smoketest/users">users</Link></li>
          <li><Link href="smoketest/roles">roles</Link></li>
        </ul>
      </main>
    </div>
  );
}