// App-wide authentication state, shared via React Context.
//
// Any screen can call useAuth() to read whether we're logged in and to call
// login/register/logout. On startup it restores a previously saved token so
// the user stays logged in between app launches.

import React, { createContext, useContext, useEffect, useState, ReactNode } from 'react';
import { loginRequest, registerRequest } from '../api/auth';
import { setAuthToken } from '../api/client';
import { clearToken, loadToken, saveToken } from './tokenStorage';

interface AuthState {
  token: string | null;
  email: string | null;
  loading: boolean;                 // true while restoring the saved token at startup
  login: (email: string, password: string) => Promise<void>;
  register: (email: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
}

const AuthContext = createContext<AuthState | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(null);
  const [email, setEmail] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  // On first mount, try to restore a saved token.
  useEffect(() => {
    (async () => {
      const saved = await loadToken();
      if (saved) {
        setAuthToken(saved);
        setToken(saved);
      }
      setLoading(false);
    })();
  }, []);

  async function handleAuthSuccess(newToken: string, userEmail: string) {
    await saveToken(newToken);   // persist on device
    setAuthToken(newToken);      // attach to future API calls
    setToken(newToken);
    setEmail(userEmail);
  }

  async function login(emailArg: string, password: string) {
    const result = await loginRequest(emailArg, password);
    await handleAuthSuccess(result.token, result.email);
  }

  async function register(emailArg: string, password: string) {
    const result = await registerRequest(emailArg, password);
    await handleAuthSuccess(result.token, result.email);
  }

  async function logout() {
    await clearToken();
    setAuthToken(null);
    setToken(null);
    setEmail(null);
  }

  return (
    <AuthContext.Provider value={{ token, email, loading, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

/** Hook used by screens to access auth state. Throws if used outside the provider. */
export function useAuth(): AuthState {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used inside an AuthProvider');
  return ctx;
}
