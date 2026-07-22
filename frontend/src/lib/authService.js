import { api } from './apiClient';
import { rolesFromToken, decodeJwt } from './jwt';

// Maps a raw JwtTokenResponse (accessToken, refreshToken, userId, username)
// plus the roles embedded in the JWT claims into the user object the UI uses.
export function buildUserFromToken(tokenResponse) {
  const roles = rolesFromToken(tokenResponse.accessToken);
  const claims = decodeJwt(tokenResponse.accessToken);
  return {
    id: tokenResponse.userId,
    username: tokenResponse.username,
    role: roles[0] || 'STUDENT', // primary role drives navigation/permissions
    roles,
    name: claims?.username || tokenResponse.username,
  };
}

export const authService = {
  login: (usernameOrEmail, password) => api.post('/auth/login', { usernameOrEmail, password }),
  register: (payload) => api.post('/auth/register', payload),
  refresh: (refreshToken) => api.post('/auth/refresh', { refreshToken }),
};
