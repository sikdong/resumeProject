import { api } from './client';
import type { EvaluationDto } from '../types/evaluation';

const buildAuthOptions = () => {
  if (typeof window === 'undefined') {
    return undefined;
  }

  const token = localStorage.getItem('accessToken');
  if (!token) {
    return undefined;
  }

  const headers: HeadersInit = {
    Authorization: `Bearer ${token}`,
  };

  return { headers };
};

export const createEvaluation = (resumeId: number, payload: EvaluationDto) =>
  api.post<void>(
    `/api/resumes/${resumeId}/evaluations`,
    JSON.stringify(payload),
    buildAuthOptions()
  );

export const updateEvaluation = (evaluationId: number, payload: EvaluationDto) =>
  api.put<void>(
    `/api/evaluations/${evaluationId}`,
    JSON.stringify(payload),
    buildAuthOptions()
  );

export const deleteEvaluation = (evaluationId: number) =>
  api.del<void>(`/api/evaluations/${evaluationId}`, buildAuthOptions());

export const fetchMyEvaluations = () => api.get(`/api/evaluations/me`, buildAuthOptions());
