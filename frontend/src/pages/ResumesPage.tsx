import { FormEvent, useEffect, useState } from 'react';
import { MagnifyingGlassIcon, PlusCircleIcon } from '@heroicons/react/24/outline';
import { fetchResumes, fetchRecentlyViewed } from '../api/resumes';
import type { ResumeRecentlyViewed, ResumeSummary } from '../types/resume';
import LoadingSpinner from '../components/common/LoadingSpinner';
import ErrorAlert from '../components/common/ErrorAlert';
import EmptyState from '../components/common/EmptyState';
import ResumeCard from '../components/resume/ResumeCard';
import RecentlyViewed from '../components/resume/RecentlyViewed';
import UploadResumeDialog from '../components/resume/UploadResumeDialog';

type LoadingState = 'idle' | 'loading' | 'error';

const ResumesPage = () => {
  const [resumes, setResumes] = useState<ResumeSummary[]>([]);
  const [recentlyViewed, setRecentlyViewed] = useState<ResumeRecentlyViewed[]>([]);
  const [listState, setListState] = useState<LoadingState>('loading');
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [searchQuery, setSearchQuery] = useState('');
  const [isUploadOpen, setUploadOpen] = useState(false);

  useEffect(() => {
    let isMounted = true;
    const load = async () => {
      setListState('loading');
      try {
        const items = await fetchResumes(searchQuery);
        if (!isMounted) return;
        setResumes(items);
        setErrorMessage(null);
        setListState('idle');
      } catch (error) {
        if (!isMounted) return;
        const message = error instanceof Error ? error.message : '이력서를 불러오지 못했습니다.';
        setErrorMessage(message);
        setListState('error');
      }
    };

    load();
    return () => {
      isMounted = false;
    };
  }, [searchQuery]);

  useEffect(() => {
    let isMounted = true;

    const loadRecent = async () => {
      try {
        const items = await fetchRecentlyViewed();
        if (!isMounted) return;
        setRecentlyViewed(items);
      } catch (error) {
        console.warn('최근 본 이력서를 불러오지 못했습니다.', error);
      }
    };

    loadRecent();

    if (typeof window !== 'undefined') {
      const handleFocus = () => {
        loadRecent();
      };
      window.addEventListener('focus', handleFocus);
      return () => {
        isMounted = false;
        window.removeEventListener('focus', handleFocus);
      };
    }

    return () => {
      isMounted = false;
    };
  }, []);

  const handleSearchSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setSearchQuery(searchTerm.trim());
  };

  const openResumeDetail = (resumeId: number) => {
    if (typeof window === 'undefined') return;
    window.open(`/resumes/${resumeId}`, '_blank', 'noopener,noreferrer');
  };

  const handleUploadSuccess = () => {
    setSearchQuery('');
    setSearchTerm('');
  };

  const gridColumns = 'lg:grid-cols-[minmax(0,_4.5fr)_minmax(0,_1.25fr)]';

  return (
    <div className="space-y-6">
      <section className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
        <form onSubmit={handleSearchSubmit} className="flex flex-col gap-4 sm:flex-row sm:items-center">
          <div className="relative flex-1">
            <MagnifyingGlassIcon className="pointer-events-none absolute left-3 top-3 h-5 w-5 text-slate-400" />
            <input
              type="text"
              value={searchTerm}
              onChange={(event) => setSearchTerm(event.target.value)}
              placeholder="이력서 제목, 키워드로 검색하세요"
              className="w-full rounded-xl border border-slate-200 bg-slate-50 py-2 pl-10 pr-4 text-sm text-slate-700 shadow-inner focus:border-brand"
            />
          </div>
          <div className="flex gap-2">
            <button
              type="submit"
              className="inline-flex items-center gap-2 rounded-xl bg-brand px-4 py-2 text-sm font-semibold text-white shadow-soft transition hover:bg-brand-dark"
            >
              검색
            </button>
            <button
              type="button"
              onClick={() => setUploadOpen(true)}
              className="inline-flex items-center gap-2 rounded-xl border border-brand bg-white px-4 py-2 text-sm font-semibold text-brand transition hover:bg-brand/10"
            >
              <PlusCircleIcon className="h-5 w-5" /> 이력서 업로드
            </button>
          </div>
        </form>
      </section>

      {listState === 'error' && errorMessage ? <ErrorAlert message={errorMessage} /> : null}

      <div className={`grid gap-6 ${gridColumns}`}>
        <section className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
          <div className="flex items-center justify-between">
            <div>
              <h2 className="text-lg font-semibold text-slate-800">전체 이력서</h2>
              <p className="text-sm text-slate-500">총 {resumes.length}건</p>
            </div>
          </div>

          <div className="mt-6 grid gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
            {listState === 'loading' ? (
              <div className="col-span-full">
                <LoadingSpinner label="이력서를 불러오는 중입니다" />
              </div>
            ) : resumes.length === 0 ? (
              <div className="col-span-full">
                <EmptyState title="등록된 이력서가 없습니다" description="우측 상단의 버튼으로 이력서를 추가해 보세요." />
              </div>
            ) : (
              resumes.map((resume) => (
                <ResumeCard key={resume.id} resume={resume} onSelect={(item) => openResumeDetail(item.id)} />
              ))
            )}
          </div>
        </section>

        <aside className="h-full">
          <RecentlyViewed items={recentlyViewed} onSelect={openResumeDetail} />
        </aside>
      </div>

      <UploadResumeDialog
        open={isUploadOpen}
        onClose={() => setUploadOpen(false)}
        onSuccess={handleUploadSuccess}
      />
    </div>
  );
};

export default ResumesPage;
