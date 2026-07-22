import axios from 'axios';

// Backend serves everything under context-path "/api" (see application.yml).
// Set VITE_API_BASE_URL in .env to point at a different host (e.g. in Docker
// the nginx frontend proxies /api to the "backend" service — see nginx.conf).
const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

const TOKEN_KEY = 'hms_access_token';
const REFRESH_KEY = 'hms_refresh_token';

export const tokenStorage = {
  getAccessToken: () => localStorage.getItem(TOKEN_KEY),
  getRefreshToken: () => localStorage.getItem(REFRESH_KEY),
  setTokens: (accessToken, refreshToken) => {
    localStorage.setItem(TOKEN_KEY, accessToken);
    if (refreshToken) localStorage.setItem(REFRESH_KEY, refreshToken);
  },
  clear: () => {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(REFRESH_KEY);
  },
};

const http = axios.create({ baseURL, headers: { 'Content-Type': 'application/json' } });

// Attach the access token to every outgoing request.
http.interceptors.request.use((config) => {
  const token = tokenStorage.getAccessToken();
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

// The backend wraps every success response as { success, message, data, timestamp }
// and every error as { status, error, message, path, validationErrors }.
// We unwrap `data` here so the rest of the app just works with plain payloads,
// and normalize errors into a single ApiError shape.
export class ApiError extends Error {
  constructor(message, status, validationErrors) {
    super(message);
    this.status = status;
    this.validationErrors = validationErrors || null;
  }
}

let isRefreshing = false;
let pendingQueue = [];

function resolveQueue(error, token) {
  pendingQueue.forEach(({ resolve, reject }) => (error ? reject(error) : resolve(token)));
  pendingQueue = [];
}

// Anything that should force the user back to /login when refresh fails.
let onAuthExpired = () => {};
export function setOnAuthExpired(fn) {
  onAuthExpired = fn;
}

http.interceptors.response.use(
  (response) => response,
  async (error) => {
    const original = error.config;
    const status = error.response?.status;

    // Don't try to refresh on the auth endpoints themselves.
    const isAuthCall = original?.url?.includes('/auth/');

    if (status === 401 && !original._retry && !isAuthCall && tokenStorage.getRefreshToken()) {
      if (isRefreshing) {
        // Queue this request until the in-flight refresh resolves.
        return new Promise((resolve, reject) => {
          pendingQueue.push({ resolve, reject });
        }).then((token) => {
          original.headers.Authorization = `Bearer ${token}`;
          original._retry = true;
          return http(original);
        });
      }

      original._retry = true;
      isRefreshing = true;
      try {
        const { data } = await axios.post(`${baseURL}/auth/refresh`, {
          refreshToken: tokenStorage.getRefreshToken(),
        });
        const newAccessToken = data.data.accessToken;
        tokenStorage.setTokens(newAccessToken, data.data.refreshToken);
        resolveQueue(null, newAccessToken);
        original.headers.Authorization = `Bearer ${newAccessToken}`;
        return http(original);
      } catch (refreshError) {
        resolveQueue(refreshError, null);
        tokenStorage.clear();
        onAuthExpired();
        return Promise.reject(new ApiError('Session expired. Please sign in again.', 401));
      } finally {
        isRefreshing = false;
      }
    }

    const body = error.response?.data;
    const message = body?.message || error.message || 'Something went wrong';
    return Promise.reject(new ApiError(message, status, body?.validationErrors));
  }
);

async function unwrap(promise) {
  const res = await promise;
  return res.data?.data;
}

export const api = {
  get: (url, config) => unwrap(http.get(url, config)),
  post: (url, body, config) => unwrap(http.post(url, body, config)),
  put: (url, body, config) => unwrap(http.put(url, body, config)),
  patch: (url, body, config) => unwrap(http.patch(url, body, config)),
  delete: (url, config) => unwrap(http.delete(url, config)),
};

export default http;
