import { api } from './client';
import type { MemberSummary } from '../types/user';

export interface RegisterPayload {
  email: string;
  name: string;
  password: string;
}

export interface LoginPayload {
  email: string;
  password: string;
}

export const register = (payload: RegisterPayload) =>
  api.post<MemberSummary>('/api/auth/register', JSON.stringify(payload));

export const login = (payload: LoginPayload) =>
  api.post<MemberSummary>('/api/auth/login', JSON.stringify(payload));

export const logout = () => api.post<void>('/api/auth/logout');
