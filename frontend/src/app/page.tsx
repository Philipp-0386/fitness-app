import Link from 'next/link';

export default function Home() {
  return (
    <div>
      <h1>Home Page</h1>
      <ul>
        <li>
          <Link href="/signup">SignUp</Link>
        </li>
        <li>
          <Link href="/login">Login</Link>
        </li>
      </ul>
    </div>
  );
}
