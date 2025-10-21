import { api, type ApiError } from './client';
import type {
  ResumeResponse,
  ResumeSummary,
  ResumeRecentlyViewed,
  ResumeUploadPayload,
} from '../types/resume';

const ensureArray = <T>(value: unknown): T[] => (Array.isArray(value) ? (value as T[]) : []);

const ACCESS_TOKEN_KEY = 'accessToken';
const REFRESH_TOKEN_KEY = 'refreshToken';

type TokenRefreshResponse = {
  token?: string | null;
};

const readStoredToken = (key: string) => {
  if (typeof window === 'undefined') {
    return null;
  }
  try {
    const value = window.localStorage.getItem(key);
    if (!value) {
      return null;
    }
    const trimmed = value.trim();
    return trimmed.length > 0 ? trimmed : null;
  } catch {
    return null;
  }
};

const writeStoredToken = (key: string, value: string | null) => {
  if (typeof window === 'undefined') {
    return;
  }
  try {
    if (value && value.trim().length > 0) {
      window.localStorage.setItem(key, value.trim());
    } else {
      window.localStorage.removeItem(key);
    }
  } catch {
    // ignore storage errors
  }
};

const notifyAccessTokenUpdate = (value: string | null) => {
  if (typeof window === 'undefined') {
    return;
  }
  try {
    window.dispatchEvent(new CustomEvent<string | null>('wishy:access-token-updated', { detail: value }));
  } catch {
    window.dispatchEvent(new Event('wishy:access-token-updated'));
  }
};

const confirmSessionAuthentication = async (): Promise<boolean> => {
  try {
    await api.get('/api/user/me');
    return true;
  } catch (error) {
    const status = (error as ApiError | undefined)?.status;
    if (status === 401) {
      return false;
    }
    console.warn('세션 인증 상태를 확인하지 못했습니다.', error);
    return false;
  }
};

const ensureAccessToken = async (): Promise<string | null> => {
  const existingToken = readStoredToken(ACCESS_TOKEN_KEY);
  if (existingToken) {
    return existingToken;
  }

  const refreshToken = readStoredToken(REFRESH_TOKEN_KEY);
  if (!refreshToken) {
    const canProceedWithSession = await confirmSessionAuthentication();
    if (canProceedWithSession) {
      return null;
    }

    throw new Error('로그인이 필요합니다. 다시 로그인해주세요.');
  }

  try {
    const response = await api.post<TokenRefreshResponse>(
      '/api/token/refresh',
      JSON.stringify({ token: refreshToken })
    );

    const nextToken = response?.token?.trim();
    if (!nextToken) {
      throw new Error('세션이 만료되었습니다. 다시 로그인해주세요.');
    }

    writeStoredToken(ACCESS_TOKEN_KEY, nextToken);
    notifyAccessTokenUpdate(nextToken);
    return nextToken;
  } catch {
    writeStoredToken(ACCESS_TOKEN_KEY, null);
    writeStoredToken(REFRESH_TOKEN_KEY, null);
    notifyAccessTokenUpdate(null);

    const canProceedWithSession = await confirmSessionAuthentication();
    if (canProceedWithSession) {
      return null;
    }

    throw new Error('세션이 만료되었습니다. 다시 로그인해주세요.');
  }
};

export const fetchResumes = async (keyword = '') => {
  const query = keyword.trim().length ? `?searchValue=${encodeURIComponent(keyword.trim())}` : '';
  const data = await api.get<ResumeSummary[] | unknown>(`/api/resumes${query}`);
  return ensureArray<ResumeSummary>(data);
};

export const fetchResumeDetail = (resumeId: number) => api.get<ResumeResponse>(`/api/resumes/${resumeId}`);

export const fetchRecentlyViewed = async () => {
  const data = await api.get<ResumeRecentlyViewed[] | unknown>(`/api/resumes/me/recently-viewed`);
  return ensureArray<ResumeRecentlyViewed>(data);
};

export const fetchMyResumes = async () => {
  const data = await api.get<ResumeSummary[] | unknown>(`/api/resumes/me`);
  return ensureArray<ResumeSummary>(data);
};

export const downloadResumeUrl = (resumeId: number) => `/api/resumes/${resumeId}/file`;

export const uploadResume = async (payload: ResumeUploadPayload) => {
  await ensureAccessToken();

  const formData = new FormData();
  formData.append('file', payload.file);
  formData.append('title', payload.title);
  formData.append('comment', payload.comment ?? '');
  formData.append('isMailSent', String(payload.isMailSent));

  return api.post<void>('/api/resumes', formData, {
    headers: {
      // Boundary는 브라우저가 설정
    },
  });
};

export const deleteResume = (resumeId: number) => api.del<void>(`/api/resumes/${resumeId}`);
