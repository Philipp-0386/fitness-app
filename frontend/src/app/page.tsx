import Link from 'next/link';

export default function Home() {
  return (
    <div>
      <h2>Home</h2>
      <ul>
        <li>
          <Link href="/signup">SignUp</Link>
        </li>
      </ul>
    </div>
  );
}
