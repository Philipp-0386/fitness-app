const EXPIRY_MARGIN_MS = 30_000;

/**
 * Reads the subject claim of a JWT
 */
export function readSubjectClaim(token: string): string | null {
  const sub = readClaims(token)?.sub;
  return typeof sub === 'string' ? sub : null;
}

/**
 * Checks if a JWT expires within the next 30 seconds, so it cannot run out between the check
 * in the frontend and the validation in the backend
 */
export function isExpiringSoon(token: string): boolean {
  const exp = readClaims(token)?.exp;
  if (typeof exp !== 'number') return true;

  return exp * 1000 - Date.now() < EXPIRY_MARGIN_MS;
}

function readClaims(token: string): Record<string, unknown> | null {
  const payload = token.split('.')[1];
  if (!payload) return null;

  try {
    const json = Buffer.from(payload, 'base64url').toString('utf8');
    const claims: unknown = JSON.parse(json);
    return typeof claims === 'object' && claims !== null
      ? (claims as Record<string, unknown>)
      : null;
  } catch {
    return null;
  }
}
