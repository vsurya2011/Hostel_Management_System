// Minimal JWT payload decoder. We only ever read claims the backend put there
// (username, roles, exp) — we never need to verify the signature client-side,
// the API does that on every request.
export function decodeJwt(token) {
  try {
    const payload = token.split('.')[1];
    const base64 = payload.replace(/-/g, '+').replace(/_/g, '/');
    const padded = base64.padEnd(base64.length + ((4 - (base64.length % 4)) % 4), '=');
    const json = decodeURIComponent(
      atob(padded)
        .split('')
        .map((c) => '%' + c.charCodeAt(0).toString(16).padStart(2, '0'))
        .join('')
    );
    return JSON.parse(json);
  } catch {
    return null;
  }
}

export function isExpired(token) {
  const claims = decodeJwt(token);
  if (!claims?.exp) return true;
  return Date.now() >= claims.exp * 1000;
}

// Backend embeds roles like "ROLE_ADMIN" — normalize to "ADMIN".
export function rolesFromToken(token) {
  const claims = decodeJwt(token);
  if (!claims?.roles) return [];
  return claims.roles.map((r) => r.replace(/^ROLE_/, ''));
}
