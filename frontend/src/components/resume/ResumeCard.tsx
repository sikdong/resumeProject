import { EyeIcon, ChatBubbleLeftRightIcon } from '@heroicons/react/24/outline';
import type { ResumeSummary } from '../../types/resume';

interface ResumeCardProps {
  resume: ResumeSummary;
  onSelect?: (resume: ResumeSummary) => void;
  isActive?: boolean;
}

const ResumeCard = ({ resume, onSelect, isActive }: ResumeCardProps) => (
  <button
    type="button"
    onClick={() => onSelect?.(resume)}
    className={`group flex h-full w-full flex-col items-center justify-between rounded-2xl border border-transparent bg-white p-6 text-center shadow-sm transition hover:-translate-y-0.5 hover:shadow-lg focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-brand focus-visible:ring-offset-2 focus-visible:ring-offset-slate-50 ${
      isActive ? 'ring-2 ring-brand ring-offset-2 ring-offset-slate-50' : ''
    }`}
  >
    <div className="w-full space-y-3">
      <h3 className="text-lg font-semibold text-slate-800 group-hover:text-brand-dark">
        {resume.title}
      </h3>
      {resume.keyword ? (
        <span className="inline-flex items-center justify-center rounded-full bg-brand/10 px-3 py-1 text-xs font-semibold text-brand">
          {resume.keyword}
        </span>
      ) : null}
    </div>
    <div className="mt-6 flex w-full flex-col items-center gap-3 text-sm text-slate-500">
      <span className="font-medium text-slate-600">{resume.member?.name ?? '알 수 없음'}</span>
      <div className="flex items-center gap-6 text-xs font-medium text-slate-500">
        <span className="inline-flex items-center gap-1">
          <EyeIcon className="h-4 w-4" />
          {resume.viewCount ?? 0}
        </span>
        <span className="inline-flex items-center gap-1">
          <ChatBubbleLeftRightIcon className="h-4 w-4" />
          {resume.commentSize ?? 0}
        </span>
      </div>
      <div className="inline-flex items-center gap-1">
        {resume.createAt ?? 0}
      </div>
      <div className="flex items-center gap-6 text-xs font-medium text-slate-500">
        <span className="inline-flex items-center gap-1">
          <b>
            {resume.isViewed && "봤어요"}
          </b>
        </span>
        <span className="inline-flex items-center gap-1">
          <b>
            {resume.isEvaluated && "평가 했어요"}
          </b>
        </span>
      </div>
    </div>
  </button>
);

export default ResumeCard;
