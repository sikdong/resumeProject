import { useState } from 'react';
import { ArrowDownTrayIcon, ChatBubbleBottomCenterTextIcon } from '@heroicons/react/24/outline';
import type { EvaluationSummary } from '../../types/evaluation';
import type { ResumeResponse } from '../../types/resume';
import EmptyState from '../common/EmptyState';

interface ResumeDetailPanelProps {
  resume?: ResumeResponse;
  isAuthenticated: boolean;
  isSubmitting: boolean;
  onCreateEvaluation?: (score: number, comment: string) => Promise<void>;
  onDeleteEvaluation?: (evaluationId: number) => Promise<void>;
  onDownload?: () => void;
}

const formatDateTime = (value?: string | null) => {
  if (!value) return '';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return value;
  }
  return date.toLocaleString();
};

const EvaluationItem = ({
  evaluation,
  canDelete,
  onDelete,
}: {
  evaluation: EvaluationSummary;
  canDelete: boolean;
  onDelete?: (id: number) => void;
}) => (
  <li className="rounded-xl border border-slate-200 bg-white p-4 shadow-sm">
    <div className="flex items-start justify-between gap-4">
      <div>
        <p className="text-sm font-semibold text-slate-700">{evaluation.memberName}</p>
        <p className="mt-1 text-xs text-slate-400">{formatDateTime(evaluation.evaluatedAt)}</p>
      </div>
      <div className="flex items-center gap-1 rounded-full bg-brand/10 px-3 py-1 text-sm font-semibold text-brand">
        {evaluation.score.toFixed(1)}
      </div>
    </div>
    {evaluation.comment ? (
      <p className="mt-3 text-sm leading-relaxed text-slate-600">{evaluation.comment}</p>
    ) : null}
    {canDelete ? (
      <div className="mt-3 text-right">
        <button
          type="button"
          onClick={() => onDelete?.(evaluation.id)}
          className="text-xs font-semibold text-red-500 hover:text-red-600"
        >
          삭제
        </button>
      </div>
    ) : null}
  </li>
);

const ResumeDetailPanel = ({
  resume,
  isAuthenticated,
  isSubmitting,
  onCreateEvaluation,
  onDeleteEvaluation,
  onDownload,
}: ResumeDetailPanelProps) => {
  const [score, setScore] = useState(4.5);
  const [comment, setComment] = useState('');

  if (!resume) {
    return null;
  }

  const handleSubmit = async () => {
    if (!onCreateEvaluation) return;
    await onCreateEvaluation(score, comment);
    setComment('');
    setScore(4.5);
  };

  const averageScore = resume.averageScore?.toFixed(1) ?? '0.0';

  return (
    <div className="space-y-6 rounded-3xl border border-slate-200 bg-white p-6 shadow-lg">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <p className="text-sm font-semibold uppercase tracking-wide text-brand">#{resume.id}</p>
          <h2 className="mt-1 text-2xl font-semibold text-slate-900">{resume.title}</h2>
          <p className="mt-2 text-sm text-slate-500">{resume.comment || '설명 없음'}</p>
        </div>
        <div className="flex items-center gap-3">
          <div className="rounded-2xl bg-slate-100 px-4 py-2 text-center">
            <p className="text-xs text-slate-500">평균 점수</p>
            <p className="text-2xl font-semibold text-brand">{averageScore}</p>
          </div>
          <button
            type="button"
            onClick={onDownload}
            className="inline-flex items-center gap-2 rounded-xl bg-brand px-4 py-2 text-sm font-semibold text-white shadow-soft transition hover:bg-brand-dark"
          >
            <ArrowDownTrayIcon className="h-4 w-4" /> PDF 열기
          </button>
        </div>
      </div>
      <dl className="grid gap-4 rounded-2xl bg-slate-50 p-4 text-sm sm:grid-cols-3">
        <div>
          <dt className="text-xs uppercase tracking-wide text-slate-400">작성자</dt>
          <dd className="mt-1 font-medium text-slate-700">{resume.member?.name}</dd>
          <dd className="text-xs text-slate-400">{resume.member?.jobTitle ?? '직무 미등록'}</dd>
        </div>
        <div>
          <dt className="text-xs uppercase tracking-wide text-slate-400">커리어 레벨</dt>
          <dd className="mt-1 font-medium text-slate-700">{resume.member?.careerLevel}</dd>
        </div>
        <div>
          <dt className="text-xs uppercase tracking-wide text-slate-400">조회수</dt>
          <dd className="mt-1 font-medium text-slate-700">{resume.viewCount ?? 0}</dd>
          <dd className="text-xs text-slate-400">최근 열람 {resume.isViewed ? 'Y' : 'N'}</dd>
        </div>
      </dl>

      <section className="space-y-4">
        <header className="flex items-center justify-between">
          <div>
            <h3 className="text-lg font-semibold text-slate-800">평가</h3>
            <p className="text-sm text-slate-500">총 {resume.commentSize}개의 코멘트</p>
          </div>
          <span className="flex items-center gap-1 rounded-full bg-slate-100 px-3 py-1 text-xs font-medium text-slate-600">
            <ChatBubbleBottomCenterTextIcon className="h-4 w-4" />
            평균 {averageScore}
          </span>
        </header>

        {resume.evaluations && resume.evaluations.length > 0 ? (
          <ul className="space-y-3">
            {resume.evaluations.map((evaluation) => (
              <EvaluationItem
                key={evaluation.id}
                evaluation={evaluation}
                canDelete={isAuthenticated && Boolean(onDeleteEvaluation)}
                onDelete={(id) => onDeleteEvaluation?.(id)}
              />
            ))}
          </ul>
        ) : (
          <EmptyState title="아직 등록된 평가가 없습니다" description="첫 번째 평가를 남겨보세요." />
        )}
      </section>

      <section className="rounded-2xl border border-slate-200 bg-slate-50 p-5">
        <h4 className="text-sm font-semibold text-slate-700">평가 남기기</h4>
        <p className="mt-1 text-xs text-slate-500">
          점수는 1.0 ~ 5.0 범위에서 소수 첫째 자리까지 입력할 수 있습니다.
        </p>

        <div className="mt-3 grid gap-3 sm:grid-cols-[120px_1fr]">
          <div>
            <label className="text-xs font-semibold uppercase tracking-wide text-slate-400" htmlFor="score">
              점수
            </label>
            <input
              id="score"
              type="number"
              min={1}
              max={5}
              step={0.1}
              value={score}
              onChange={(event) => setScore(Number(event.target.value) || 0)}
              className="mt-1 w-full rounded-lg border-slate-200 bg-white px-3 py-2 text-sm"
              disabled={!isAuthenticated}
            />
          </div>
          <div>
            <label className="text-xs font-semibold uppercase tracking-wide text-slate-400" htmlFor="comment">
              코멘트
            </label>
            <textarea
              id="comment"
              value={comment}
              onChange={(event) => setComment(event.target.value)}
              rows={3}
              placeholder={isAuthenticated ? '장점과 개선 포인트를 구체적으로 적어주세요.' : '로그인 후 평가를 등록할 수 있어요.'}
              className="mt-1 w-full rounded-lg border-slate-200 bg-white px-3 py-2 text-sm"
              disabled={!isAuthenticated}
            />
          </div>
        </div>

        <div className="mt-4 flex justify-end">
          <button
            type="button"
            onClick={handleSubmit}
            disabled={!isAuthenticated || isSubmitting || comment.trim().length === 0}
            className="inline-flex items-center justify-center rounded-lg bg-brand px-4 py-2 text-sm font-semibold text-white shadow-soft transition hover:bg-brand-dark disabled:cursor-not-allowed disabled:bg-slate-300"
          >
            {isSubmitting ? '등록 중...' : '평가 등록'}
          </button>
        </div>
      </section>
    </div>
  );
};

export default ResumeDetailPanel;
