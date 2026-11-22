// Auth-related API calls. These map directly to the backend's
// /api/auth/register and /api/auth/login endpoints.

import { api } from './client';
import { AuthResult } from '../types';

export async function registerRequest(email: string, password: string): Promise<AuthResult> {
  const res = await api.post<AuthResult>('/api/auth/register', { email, password });
  return res.data;
}

export async function loginRequest(email: string, password: string): Promise<AuthResult> {
  const res = await api.post<AuthResult>('/api/auth/login', { email, password });
  return res.data;
}
