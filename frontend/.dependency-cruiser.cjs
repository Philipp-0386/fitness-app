const fs = require('node:fs');
const path = require('node:path');

// dependency-cruiser only sees paths, so the 'use client' files are found by reading them.
const src = path.join(__dirname, 'src');
const USE_CLIENT = /^(?:\s|\/\/[^\n]*\n|\/\*(?:[^*]|\*(?!\/))*\*\/)*['"]use client['"]/;
const useClientFiles = fs
  .readdirSync(src, { recursive: true })
  .filter(
    (file) =>
      /\.tsx?$/.test(file) &&
      USE_CLIENT.test(fs.readFileSync(path.join(src, file), 'utf8')),
  )
  .map((file) =>
    `src/${file.split(path.sep).join('/')}`.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'),
  );

/** @type {import('dependency-cruiser').IConfiguration} */
module.exports = {
  forbidden: [
    {
      name: 'no-server-api-in-client',
      comment:
        '*.api.ts modules are server-only. Client code writes through *.client.ts and the /api route handlers instead.',
      severity: 'error',
      from: { path: ['\\.client\\.ts$', `^(?:${useClientFiles.join('|')})$`] },
      to: { path: '\\.api\\.ts$', reachable: true },
    },
    {
      name: 'no-circular',
      severity: 'error',
      from: {},
      to: { circular: true },
    },
  ],
  options: {
    includeOnly: '^src/',
    tsConfig: { fileName: 'tsconfig.json' },
    enhancedResolveOptions: {
      extensions: ['.ts', '.tsx', '.js', '.jsx', '.json'],
    },
  },
};
