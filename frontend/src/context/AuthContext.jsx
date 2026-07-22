import React, { createContext, useContext, useEffect, useState, useCallback } from 'react';
import { authService, buildUserFromToken } from '../lib/authService';
import { tokenStorage, setOnAuthExpired, ApiError } from '../lib/apiClient';
import { isExpired } from '../lib/jwt';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [isInitializing, setIsInitializing] = useState(true);

  const logout = useCallback(() => {
    tokenStorage.clear();
    localStorage.removeItem('hms_user');
    setUser(null);
  }, []);

  // Restore session on load if we have a still-valid (or refreshable) token.
  useEffect(() => {
    const accessToken = tokenStorage.getAccessToken();
    const savedUser = localStorage.getItem('hms_user');
    if (accessToken && savedUser && !isExpired(accessToken)) {
      setUser(JSON.parse(savedUser));
    } else if (accessToken) {
      // Access token expired but the axios interceptor will attempt a
      // refresh transparently on the first authenticated request.
      tokenStorage.clear();
      localStorage.removeItem('hms_user');
    }
    setIsInitializing(false);
  }, []);

  // Wire the apiClient's 401-after-refresh-fails callback to a real logout.
  useEffect(() => {
    setOnAuthExpired(() => logout());
  }, [logout]);

  const login = async (usernameOrEmail, password) => {
    const tokenResponse = await authService.login(usernameOrEmail, password);
    tokenStorage.setTokens(tokenResponse.accessToken, tokenResponse.refreshToken);
    const nextUser = buildUserFromToken(tokenResponse);
    localStorage.setItem('hms_user', JSON.stringify(nextUser));
    setUser(nextUser);
    return nextUser;
  };

  const register = async (payload) => {
    const tokenResponse = await authService.register(payload);

    // Wardens don't get tokens back until an admin approves their account —
    // surface that as a distinct outcome instead of trying to log them in.
    if (tokenResponse.pendingApproval || !tokenResponse.accessToken) {
      return { pendingApproval: true, message: tokenResponse.message };
    }

    tokenStorage.setTokens(tokenResponse.accessToken, tokenResponse.refreshToken);
    const nextUser = buildUserFromToken(tokenResponse);
    localStorage.setItem('hms_user', JSON.stringify(nextUser));
    setUser(nextUser);
    return nextUser;
  };

  return (
    <AuthContext.Provider value={{ user, login, register, logout, isInitializing }}>{children}</AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}

export { ApiError };
