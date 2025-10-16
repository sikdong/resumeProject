import { FormEvent, useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useAuthToken } from '../context/AuthTokenContext';
import { login } from '../api/auth';

const LoginPage = () => {
  const [loginId, setLoginId] = useState('');
  const [loginPassword, setLoginPassword] = useState('');
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const { setToken, setSessionName } = useAuthToken();
  const [error, setError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    const accessToken = searchParams.get('token');
    const refreshToken = searchParams.get('refreshToken');

    if (!accessToken) {
      return;
    }

    setToken(accessToken);

    try {
      if (refreshToken && refreshToken.trim().length > 0) {
        localStorage.setItem('refreshToken', refreshToken.trim());
      } else {
        localStorage.removeItem('refreshToken');
      }
    } catch {
      // ignore storage errors
    }

    const url = new URL(window.location.href);
    url.searchParams.delete('token');
    url.searchParams.delete('refreshToken');
    window.history.replaceState({}, document.title, url.pathname + url.search);

    if (window.opener && !window.opener.closed) {
      window.close();
    }
  }, [searchParams, setToken]);

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setError(null);

    if (!loginId || !loginPassword) {
      setError('이메일과 비밀번호를 입력해주세요.');
      return;
    }

    try {
      setIsSubmitting(true);
      const member = await login({ email: loginId, password: loginPassword });
      setToken(null);
      setSessionName(member.name ?? member.email);
      setLoginId('');
      setLoginPassword('');
      navigate('/');
    } catch (err) {
      setError(err instanceof Error ? err.message : '로그인에 실패했습니다.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleGoogleLogin = () => {
    const apiBaseUrl = process.env.REACT_APP_API_BASE_URL ?? 'http://localhost:8080';
    window.location.href = `${apiBaseUrl}/oauth2/authorization/google`;
  };

  const handleOpenRegister = () => {
    navigate('/register');
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-slate-100 p-4">
      <div className="flex h-[500px] w-[300px] flex-col justify-between rounded-2xl border border-slate-200 bg-white p-6 text-slate-700 shadow-soft">
        <div className="space-y-1 text-center">
          <h1 className="text-2xl font-semibold text-slate-900">로그인</h1>
          <p className="text-sm text-slate-500">Evalume에 접속하세요.</p>
        </div>
        <form onSubmit={handleSubmit} className="flex flex-1 flex-col justify-center gap-4">
          {error && <p className="text-sm text-red-500">{error}</p>}
          <label className="text-left text-sm font-medium text-slate-600">
            아이디
            <input
              type="text"
              value={loginId}
              onChange={(event) => setLoginId(event.target.value)}
              placeholder="아이디를 입력하세요"
              className="mt-1 w-full rounded-xl border border-slate-200 bg-slate-50 px-3 py-2 text-sm focus:border-brand focus:outline-none"
            />
          </label>
          <label className="text-left text-sm font-medium text-slate-600">
            비밀번호
            <input
              type="password"
              value={loginPassword}
              onChange={(event) => setLoginPassword(event.target.value)}
              placeholder="비밀번호를 입력하세요"
              className="mt-1 w-full rounded-xl border border-slate-200 bg-slate-50 px-3 py-2 text-sm focus:border-brand focus:outline-none"
            />
          </label>
          <div className="space-y-2">
            <button
              type="submit"
              disabled={isSubmitting}
              className="inline-flex w-full items-center justify-center rounded-xl bg-brand px-4 py-2 text-sm font-semibold text-white transition hover:bg-brand-dark disabled:opacity-60"
            >
              {isSubmitting ? '로그인 중...' : '로그인'}
            </button>
            <button
              type="button"
              onClick={handleOpenRegister}
              className="inline-flex w-full items-center justify-center rounded-xl border border-slate-200 bg-white px-4 py-2 text-sm font-semibold text-slate-600 transition hover:bg-slate-100"
            >
              회원가입하기
            </button>
            <button
              type="button"
              className="inline-flex w-full items-center justify-center rounded-xl border border-brand bg-white px-4 py-2 text-sm font-semibold text-brand transition hover:bg-brand/10"
              onClick={handleGoogleLogin}
            >
              구글 로그인하기
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default LoginPage;
