import { NavLink, Outlet, useLocation, useNavigate } from 'react-router-dom';
import { RocketLaunchIcon } from '@heroicons/react/24/solid';
import { useEffect } from 'react';
import { useAuthToken } from '../../context/AuthTokenContext';

const navLinkClass = ({ isActive }: { isActive: boolean }) =>
  `inline-flex items-center gap-2 rounded-xl px-4 py-2 text-sm font-semibold transition ${
    isActive ? 'bg-brand text-white shadow-soft' : 'text-slate-500 hover:bg-slate-100'
  }`;

const AppLayout = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { setToken, token } = useAuthToken();
  const handleLogout = () => {
    setToken(null);
    localStorage.removeItem('refreshToken'); // 로컬 스토리지도 제거
    navigate('/');
  };

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const accessToken = params.get('token');
    const refreshToken = params.get('refreshToken');

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

    params.delete('token');
    params.delete('refreshToken');
    navigate({ pathname: location.pathname, search: params.toString() ? `?${params.toString()}` : '' }, { replace: true });
  }, [location.pathname, location.search, navigate, setToken]);

  const handleOpenLogin = () => {
    if (typeof window === 'undefined') {
      return;
    }
    window.location.href = '/login';
  };

  return (
    <div className="min-h-screen bg-slate-100">
      <header className="bg-white/90 backdrop-blur border-b border-slate-200">
        <div className="mx-auto flex max-w-[1300px] flex-col gap-4 px-6 py-6 lg:flex-row lg:items-center lg:justify-between">
          <div className="flex items-center gap-3 text-brand">
            <button
              type="button"
              onClick={() => navigate('/')}
              className="rounded-full p-1 transition hover:bg-brand/10 focus-visible:outline focus-visible:outline-2 focus-visible:outline-brand"
              aria-label="홈으로 이동"
            >
              <RocketLaunchIcon className="h-10 w-10" />
            </button>
            <div>
              <h1 className="text-2xl font-semibold text-slate-900">Evalume</h1>
              <p className="text-sm text-slate-500">이력서 평가와 관리를 한 번에</p>
            </div>
          </div>
          <div className="flex items-center gap-3">
            <nav className="flex items-center gap-3">
              {token && (
                  <NavLink to="/my-resumes" className={navLinkClass}>
                    내 이력서
                  </NavLink>
              )}
            </nav>
            {!token ? (
                <button
                    type="button"
                    onClick={handleOpenLogin}
                    className="inline-flex items-center gap-2 rounded-xl border border-brand bg-white px-4 py-2 text-sm font-semibold text-brand transition hover:bg-brand/10"
                >
                  로그인
                </button>
            ) : (
                <button
                    type="button"
                    onClick={handleLogout}
                    className="inline-flex items-center gap-2 rounded-xl border border-red-500 bg-white px-4 py-2 text-sm font-semibold text-red-500 transition hover:bg-red-100"
                >
                  로그아웃
                </button>
            )}
          </div>
        </div>
      </header>

      <main className="mx-auto grid max-w-[1300px] gap-6 px-6 py-8">
        <Outlet />
      </main>

      <footer className="border-t border-slate-200 bg-white py-6">
        <div className="mx-auto flex max-w-[1300px] flex-col items-center justify-between gap-3 px-6 text-sm text-slate-400 sm:flex-row">
          <p>© {new Date().getFullYear()} Evalume. All rights reserved.</p>
        </div>
      </footer>
    </div>
  );
};

export default AppLayout;
