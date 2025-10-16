import { FormEvent, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { register } from '../api/auth';
import { useAuthToken } from '../context/AuthTokenContext';

const RegisterPage = () => {
  const navigate = useNavigate();
  const { setSessionName, setToken } = useAuthToken();
  const [email, setEmail] = useState('');
  const [name, setName] = useState('');
  const [password, setPassword] = useState('');
  const [passwordConfirm, setPasswordConfirm] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setError(null);

    if (!email || !name || !password) {
      setError('모든 필수 항목을 입력해주세요.');
      return;
    }

    if (password !== passwordConfirm) {
      setError('비밀번호가 일치하지 않습니다.');
      return;
    }

    try {
      setIsSubmitting(true);
      const member = await register({ email, name, password });
      setToken(null);
      setSessionName(member.name ?? member.email);
      navigate('/');
    } catch (err) {
      setError(err instanceof Error ? err.message : '회원가입에 실패했습니다.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-slate-100 p-4">
      <div className="flex w-full max-w-md flex-col gap-6 rounded-2xl border border-slate-200 bg-white p-6 text-slate-700 shadow-soft">
        <div className="space-y-1 text-center">
          <h1 className="text-2xl font-semibold text-slate-900">회원가입</h1>
          <p className="text-sm text-slate-500">새 계정을 만들어 시작해 보세요.</p>
        </div>
        <form onSubmit={handleSubmit} className="flex flex-col gap-4">
          {error && <p className="text-sm text-red-500">{error}</p>}
          <label className="text-left text-sm font-medium text-slate-600">
            이메일
            <input
              type="email"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              placeholder="이메일(아이디)을 입력하세요"
              className="mt-1 w-full rounded-xl border border-slate-200 bg-slate-50 px-3 py-2 text-sm focus:border-brand focus:outline-none"
            />
          </label>
          <label className="text-left text-sm font-medium text-slate-600">
            이름
            <input
              type="text"
              value={name}
              onChange={(event) => setName(event.target.value)}
              placeholder="이름을 입력하세요"
              className="mt-1 w-full rounded-xl border border-slate-200 bg-slate-50 px-3 py-2 text-sm focus:border-brand focus:outline-none"
            />
          </label>
          <label className="text-left text-sm font-medium text-slate-600">
            비밀번호
            <input
              type="password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              placeholder="비밀번호를 입력하세요"
              className="mt-1 w-full rounded-xl border border-slate-200 bg-slate-50 px-3 py-2 text-sm focus:border-brand focus:outline-none"
            />
          </label>
          <label className="text-left text-sm font-medium text-slate-600">
            비밀번호 확인
            <input
              type="password"
              value={passwordConfirm}
              onChange={(event) => setPasswordConfirm(event.target.value)}
              placeholder="비밀번호를 다시 입력하세요"
              className="mt-1 w-full rounded-xl border border-slate-200 bg-slate-50 px-3 py-2 text-sm focus:border-brand focus:outline-none"
            />
          </label>
          <button
            type="submit"
            disabled={isSubmitting}
            className="inline-flex w-full items-center justify-center rounded-xl bg-brand px-4 py-2 text-sm font-semibold text-white transition hover:bg-brand-dark disabled:opacity-60"
          >
            {isSubmitting ? '가입 중...' : '회원가입하기'}
          </button>
          <button
            type="button"
            onClick={() => navigate('/login')}
            className="inline-flex w-full items-center justify-center rounded-xl border border-slate-200 bg-white px-4 py-2 text-sm font-semibold text-slate-600 transition hover:bg-slate-100"
          >
            로그인으로 이동
          </button>
        </form>
      </div>
    </div>
  );
};

export default RegisterPage;
