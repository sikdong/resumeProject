const LoadingSpinner = ({ label }: { label?: string }) => (
  <div className="flex flex-col items-center justify-center gap-3 py-10 text-slate-500">
    <span className="h-10 w-10 animate-spin rounded-full border-4 border-brand/20 border-t-brand" aria-hidden />
    {label ? <p className="text-sm font-medium">{label}</p> : null}
  </div>
);

export default LoadingSpinner;
