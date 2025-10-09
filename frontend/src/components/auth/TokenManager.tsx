import { FormEvent, useState } from 'react';
import { useAuthToken } from '../../context/AuthTokenContext';

const TokenManager = () => {
  const { token, setToken, isAuthenticated } = useAuthToken();
  const [value, setValue] = useState(token ?? '');

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setToken(value.trim().length ? value.trim() : null);
  };

  const handleClear = () => {
    setValue('');
    setToken(null);
  };

  return (
    <form
      onSubmit={handleSubmit}
      className="flex flex-col gap-2 rounded-xl border border-slate-200 bg-white p-4 shadow-sm md:flex-row md:items-center"
    >
      <div className="flex flex-1 flex-col">
        <label htmlFor="access-token" className="text-xs font-semibold uppercase tracking-wide text-slate-500">
          Access Token
        </label>
        <input
          id="access-token"
          type="text"
          value={value}
          onChange={(event) => setValue(event.target.value)}
          placeholder="Bearer 토큰을 입력하면 업로드/Evaluation 기능을 사용할 수 있어요"
          className="mt-1 w-full rounded-lg border-slate-200 bg-slate-50 px-3 py-2 text-sm text-slate-700 shadow-inner focus:border-brand focus:bg-white"
        />
        <p className="mt-1 text-xs text-slate-400">
          상태: {isAuthenticated ? '인증됨 (보호 API 사용 가능)' : '미인증 (읽기 전용)'}
        </p>
      </div>
      <div className="flex gap-2 pt-2 md:pt-0">
        <button
          type="submit"
          className="inline-flex items-center justify-center rounded-lg bg-brand px-4 py-2 text-sm font-semibold text-white shadow-soft transition hover:bg-brand-dark"
        >
          토큰 저장
        </button>
        <button
          type="button"
          onClick={handleClear}
          className="inline-flex items-center justify-center rounded-lg border border-slate-200 px-4 py-2 text-sm font-semibold text-slate-600 transition hover:bg-slate-100"
        >
          초기화
        </button>
      </div>
    </form>
  );
};

export default TokenManager;
