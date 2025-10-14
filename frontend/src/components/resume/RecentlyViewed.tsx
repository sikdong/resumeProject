import type { ResumeRecentlyViewed } from '../../types/resume';

interface RecentlyViewedProps {
  items: ResumeRecentlyViewed[];
  onSelect: (resumeId: number) => void;
}

const RecentlyViewed = ({ items, onSelect }: RecentlyViewedProps) => {
  const list = Array.isArray(items) ? items : [];

  return (
    <div className="flex max-h-[240px] flex-col overflow-hidden rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
      <h3 className="text-sm font-semibold text-slate-700">최근 본 이력서</h3>
      {list.length === 0 ? (
        <p className="mt-3 text-sm text-slate-500">최근 본 이력서가 없습니다.</p>
      ) : (
        <ul className="mt-3 flex flex-1 flex-col gap-2 overflow-y-auto text-sm text-slate-600 max-h-[220px]">
          {list.map((item) => (
            <li key={item.resumeId}>
              <button
                type="button"
                onClick={() => onSelect(item.resumeId)}
                className="w-full rounded-lg px-3 py-2 text-left font-medium text-slate-600 transition hover:bg-slate-100 hover:text-brand"
              >
                {item.title}
              </button>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default RecentlyViewed;
