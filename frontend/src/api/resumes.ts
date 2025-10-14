import { api } from './client';
import type {
  ResumeResponse,
  ResumeSummary,
  ResumeRecentlyViewed,
  ResumeUploadPayload,
} from '../types/resume';

const ensureArray = <T>(value: unknown): T[] => (Array.isArray(value) ? (value as T[]) : []);

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
