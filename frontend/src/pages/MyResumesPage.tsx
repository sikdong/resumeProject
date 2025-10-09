import { useEffect, useState } from 'react';
import { TrashIcon, ArrowTopRightOnSquareIcon } from '@heroicons/react/24/outline';
import { fetchMyResumes, deleteResume, downloadResumeUrl } from '../api/resumes';
import type { ResumeSummary } from '../types/resume';
import LoadingSpinner from '../components/common/LoadingSpinner';
import ErrorAlert from '../components/common/ErrorAlert';
import EmptyState from '../components/common/EmptyState';
import { useAuthToken } from '../context/AuthTokenContext';

const MyResumesPage = () => {
  const { isAuthenticated } = useAuthToken();
  const [resumes, setResumes] = useState<ResumeSummary[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [deletingId, setDeletingId] = useState<number | null>(null);

  useEffect(() => {
    const load = async () => {
      setLoading(true);
      try {
        const data = await fetchMyResumes();
        setResumes(data);
      } catch (err) {
        const message = err instanceof Error ? err.message : '내 이력서를 불러올 수 없습니다.';
        setError(message);
      } finally {
        setLoading(false);
      }
    };

    load();
  }, []);

  const handleDelete = async (resumeId: number) => {
    if (!isAuthenticated) return;
    if (!window.confirm('이력서를 삭제하시겠습니까? 삭제 후에는 복구할 수 없습니다.')) return;

    setDeletingId(resumeId);
    try {
      await deleteResume(resumeId);
      setResumes((prev) => prev.filter((item) => item.id !== resumeId));
    } catch (err) {
      const message = err instanceof Error ? err.message : '이력서를 삭제하지 못했습니다.';
      setError(message);
    } finally {
      setDeletingId(null);
    }
  };

  if (!isAuthenticated) {
    return (
      <EmptyState
        title="로그인 후 내 이력서를 확인할 수 있습니다"
        description="액세스 토큰을 입력하거나 로그인한 상태에서 다시 시도해주세요."
      />
    );
  }

  if (loading) {
    return <LoadingSpinner label="내 이력서를 불러오는 중입니다" />;
  }

  if (error) {
    return <ErrorAlert message={error} />;
  }

  if (resumes.length === 0) {
    return <EmptyState title="등록된 이력서가 없습니다" description="이력서를 업로드하면 이곳에 표시됩니다." />;
  }

  return (
    <div className="space-y-4">
      {resumes.map((resume) => (
        <div
          key={resume.id}
          className="flex flex-col gap-4 rounded-2xl border border-slate-200 bg-white p-5 shadow-sm transition hover:shadow-lg sm:flex-row sm:items-center sm:justify-between"
        >
          <div>
            <h3 className="text-lg font-semibold text-slate-800">{resume.title}</h3>
            <p className="text-sm text-slate-500">{resume.comment || '소개 문구 없음'}</p>
            <p className="mt-1 text-xs text-slate-400">평균 점수 {resume.averageScore.toFixed(1)} · 조회수 {resume.viewCount ?? 0}</p>
          </div>
          <div className="flex gap-2">
            <a
              href={downloadResumeUrl(resume.id)}
              target="_blank"
              rel="noopener noreferrer"
              className="inline-flex items-center gap-2 rounded-lg border border-slate-200 px-4 py-2 text-sm font-semibold text-slate-600 hover:bg-slate-100"
            >
              <ArrowTopRightOnSquareIcon className="h-4 w-4" /> 열기
            </a>
            <button
              type="button"
              onClick={() => handleDelete(resume.id)}
              disabled={deletingId === resume.id}
              className="inline-flex items-center gap-2 rounded-lg bg-red-500 px-4 py-2 text-sm font-semibold text-white shadow-sm transition hover:bg-red-600 disabled:cursor-not-allowed disabled:bg-red-300"
            >
              <TrashIcon className="h-4 w-4" /> {deletingId === resume.id ? '삭제 중...' : '삭제'}
            </button>
          </div>
        </div>
      ))}
    </div>
  );
};

export default MyResumesPage;
