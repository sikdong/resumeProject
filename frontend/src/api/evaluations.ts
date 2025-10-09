import { api } from './client';
import type { EvaluationDto } from '../types/evaluation';

export const createEvaluation = (resumeId: number, payload: EvaluationDto) =>
  api.post<void>(`/api/resumes/${resumeId}/evaluations`, JSON.stringify(payload));

export const updateEvaluation = (evaluationId: number, payload: EvaluationDto) =>
  api.put<void>(`/api/evaluations/${evaluationId}`, JSON.stringify(payload));

export const deleteEvaluation = (evaluationId: number) =>
  api.del<void>(`/api/evaluations/${evaluationId}`);

export const fetchMyEvaluations = () => api.get(`/api/evaluations/me`);
