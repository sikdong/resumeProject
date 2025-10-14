import { XMarkIcon } from '@heroicons/react/24/outline';
import {
  createContext,
  useCallback,
  useContext,
  useMemo,
  useState,
  type ReactNode,
} from 'react';

type ToastVariant = 'info' | 'success' | 'error';

type ToastOptions = {
  variant?: ToastVariant;
  duration?: number;
};

type ToastContextValue = {
  showToast: (message: string, options?: ToastOptions) => void;
};

type ToastItem = {
  id: number;
  message: string;
  variant: ToastVariant;
};

const ToastContext = createContext<ToastContextValue | undefined>(undefined);

const variantStyles: Record<ToastVariant, string> = {
  info: 'bg-slate-900 text-white',
  success: 'bg-emerald-600 text-white',
  error: 'bg-rose-600 text-white',
};

export const ToastProvider = ({ children }: { children: ReactNode }) => {
  const [toasts, setToasts] = useState<ToastItem[]>([]);

  const removeToast = useCallback((id: number) => {
    setToasts((prev) => prev.filter((toast) => toast.id !== id));
  }, []);

  const showToast = useCallback((message: string, options?: ToastOptions) => {
    const id = Date.now() + Math.random();
    const variant = options?.variant ?? 'info';
    const duration = options?.duration ?? 3500;

    setToasts((prev) => [...prev, { id, message, variant }]);

    if (duration > 0) {
      window.setTimeout(() => {
        removeToast(id);
      }, duration);
    }
  }, [removeToast]);

  const value = useMemo(() => ({ showToast }), [showToast]);

  return (
    <ToastContext.Provider value={value}>
      {children}
      <div
        aria-live="polite"
        className="pointer-events-none fixed left-1/2 top-6 z-50 flex w-full max-w-md -translate-x-1/2 flex-col gap-3 px-4"
      >
        {toasts.map((toast) => (
          <div
            key={toast.id}
            className={`pointer-events-auto flex items-start gap-3 rounded-xl px-4 py-3 shadow-lg ${variantStyles[toast.variant]}`}
          >
            <span className="text-sm font-semibold leading-snug">{toast.message}</span>
            <button
              type="button"
              onClick={() => removeToast(toast.id)}
              className="ml-auto rounded-full bg-white/10 p-1 text-white transition hover:bg-white/20"
              aria-label="알림 닫기"
            >
              <XMarkIcon className="h-4 w-4" />
            </button>
          </div>
        ))}
      </div>
    </ToastContext.Provider>
  );
};

export const useToast = () => {
  const context = useContext(ToastContext);
  if (!context) {
    throw new Error('useToast must be used within a ToastProvider');
  }
  return context;
};
