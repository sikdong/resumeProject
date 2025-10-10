import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import LoadingSpinner from '../components/common/LoadingSpinner';
import ErrorAlert from '../components/common/ErrorAlert';
import ResumeDetailPanel from '../components/resume/ResumeDetailPanel';
import { fetchResumeDetail, downloadResumeUrl } from '../api/resumes';
import { createEvaluation, deleteEvaluation } from '../api/evaluations';
import type { ResumeResponse } from '../types/resume';
import { useAuthToken } from '../context/AuthTokenContext';

type LoadingState = 'idle' | 'loading' | 'error';

type RouteParams = {
  resumeId?: string;
};

const ResumeDetailPage = () => {
  const { resumeId } = useParams<RouteParams>();
  const { isAuthenticated } = useAuthToken();
  const [resume, setResume] = useState<ResumeResponse | undefined>();
  const [state, setState] = useState<LoadingState>('loading');
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [isMutating, setMutating] = useState(false);

  const numericResumeId = resumeId ? Number(resumeId) : NaN;

  useEffect(() => {
    if (!resumeId) {
      setState('error');
      setErrorMessage('이력서 ID가 필요합니다.');
      return;
    }

    if (Number.isNaN(numericResumeId)) {
      setState('error');
      setErrorMessage('잘못된 이력서 ID 입니다.');
      return;
    }

    let isMounted = true;

    const load = async () => {
      setState('loading');
      try {
        const detail = await fetchResumeDetail(numericResumeId);
        if (!isMounted) return;
        setResume(detail);
        setErrorMessage(null);
        setState('idle');
      } catch (error) {
        if (!isMounted) return;
        const message = error instanceof Error ? error.message : '이력서 상세 정보를 불러오지 못했습니다.';
        setErrorMessage(message);
        setState('error');
      }
    };

    load();

    return () => {
      isMounted = false;
    };
  }, [resumeId, numericResumeId]);

  const refreshDetail = async () => {
    if (Number.isNaN(numericResumeId)) return;
    try {
      const detail = await fetchResumeDetail(numericResumeId);
      setResume(detail);
      setErrorMessage(null);
      setState('idle');
    } catch (error) {
      const message = error instanceof Error ? error.message : '이력서를 불러오지 못했습니다.';
      setErrorMessage(message);
      setState('error');
    }
  };

  const handleCreateEvaluation = async (score: number, comment: string) => {
    if (Number.isNaN(numericResumeId)) return;
    setMutating(true);
    try {
      await createEvaluation(numericResumeId, { score, comment });
      await refreshDetail();
    } catch (error) {
      const message = error instanceof Error ? error.message : '평가 등록에 실패했습니다.';
      setErrorMessage(message);
    } finally {
      setMutating(false);
    }
  };

  const handleDeleteEvaluation = async (evaluationId: number) => {
    if (Number.isNaN(numericResumeId)) return;
    setMutating(true);
    try {
      await deleteEvaluation(evaluationId);
      await refreshDetail();
    } catch (error) {
      const message = error instanceof Error ? error.message : '평가를 삭제하지 못했습니다.';
      setErrorMessage(message);
    } finally {
      setMutating(false);
    }
  };

  const handleDownload = () => {
    if (Number.isNaN(numericResumeId) || typeof window === 'undefined') return;
    window.open(downloadResumeUrl(numericResumeId), '_blank', 'noopener,noreferrer');
  };

  const renderContent = () => {
    if (state === 'loading') {
      return <LoadingSpinner label="이력서 정보를 불러오는 중입니다" />;
    }

    if (state === 'error') {
      return errorMessage ? <ErrorAlert message={errorMessage} /> : <ErrorAlert message="이력서를 불러오지 못했습니다." />;
    }

    if (!resume) {
      return <ErrorAlert message="이력서 정보를 찾을 수 없습니다." />;
    }

    return (
      <ResumeDetailPanel
        resume={resume}
        isAuthenticated={isAuthenticated}
        isSubmitting={isMutating}
        onCreateEvaluation={isAuthenticated ? handleCreateEvaluation : undefined}
        onDeleteEvaluation={isAuthenticated ? handleDeleteEvaluation : undefined}
        onDownload={handleDownload}
      />
    );
  };

  return (
    <div className="space-y-6">
      <section className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
        {renderContent()}
      </section>
    </div>
  );
};

export default ResumeDetailPage;
