import type { NextConfig } from 'next';

const nextConfig: NextConfig = {
  // Emits .next/standalone with a self-contained server.js, so the Docker
  // runtime image does not need the full node_modules tree.
  output: 'standalone',
};

export default nextConfig;
