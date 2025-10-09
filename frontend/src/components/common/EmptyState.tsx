import { ReactNode } from 'react';

const EmptyState = ({
  title,
  description,
  action,
}: {
  title: string;
  description?: string;
  action?: ReactNode;
}) => (
  <div className="flex flex-col items-center justify-center rounded-2xl border border-dashed border-slate-200 bg-white px-6 py-12 text-center shadow-sm">
    <h3 className="text-lg font-semibold text-slate-700">{title}</h3>
    {description ? <p className="mt-2 text-sm text-slate-500">{description}</p> : null}
    {action ? <div className="mt-6">{action}</div> : null}
  </div>
);

export default EmptyState;
