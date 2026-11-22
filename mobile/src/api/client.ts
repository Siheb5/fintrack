// The HTTP client used to talk to the backend.
//
// An axios "instance" is a pre-configured caller: it already knows the base URL
// and, via an interceptor, automatically adds the "Authorization: Bearer <jwt>"
// header to every request once we're logged in — so individual screens never
// have to think about the token.

import axios from 'axios';
import { API_BASE_URL } from '../config';

export const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' },
});

// The token lives here in memory; set it on login, clear it on logout.
let authToken: string | null = null;

export function setAuthToken(token: string | null): void {
  authToken = token;
}

// Interceptor: runs before every request and attaches the token if present.
api.interceptors.request.use((config) => {
  if (authToken) {
    config.headers.Authorization = `Bearer ${authToken}`;
  }
  return config;
});
