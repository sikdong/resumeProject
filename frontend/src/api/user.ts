import { api } from './client';
import type { MemberSummary } from '../types/user';

export const fetchCurrentUser = () => api.get<MemberSummary>('/api/user/me');
