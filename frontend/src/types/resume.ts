import type { EvaluationSummary } from './evaluation';
import type { MemberSummary } from './user';

export interface ResumeSummary {
  id: number;
  title: string;
  fileUrl: string;
  keyword?: string | null;
  createAt?: string | null;
  averageScore: number;
  commentSize: number;
  evaluations?: EvaluationSummary[];
  member: MemberSummary;
  viewCount?: number | null;
  comment?: string | null;
  isViewed?: boolean | null;
  isEvaluated?: boolean | null;
}

export type ResumeResponse = ResumeSummary;

export interface ResumeRecentlyViewed {
  resumeId: number;
  title: string;
}

export interface ResumeUploadPayload {
  title: string;
  comment?: string;
  isMailSent: boolean;
  file: File;
}
