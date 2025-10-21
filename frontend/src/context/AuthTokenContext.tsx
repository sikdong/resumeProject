import { createContext, useContext, useEffect, useMemo, useState, ReactNode, useCallback } from 'react';
import { fetchCurrentUser } from '../api/user';
import type { ApiError } from '../api/client';

type AuthTokenContextValue = {
  token: string | null;
  setToken: (value: string | null) => void;
  sessionName: string | null;
  setSessionName: (value: string | null) => void;
  isAuthenticated: boolean;
};

const ACCESS_TOKEN_KEY = 'accessToken';

const readCookie = (name: string) => {
  if (typeof document === 'undefined') {
    return null;
  }
  const pattern = new RegExp(`(?:^|; )${name}=([^;]*)`);
  const match = document.cookie.match(pattern);
  if (!match) {
    return null;
  }
  const rawValue = match[1].replace(/\+/g, ' ');
  try {
    return decodeURIComponent(rawValue);
  } catch {
    return rawValue;
  }
};

const readAccessToken = () => {
  if (typeof window === 'undefined') {
    return null;
  }
  try {
    const value = window.localStorage.getItem(ACCESS_TOKEN_KEY);
    if (!value) {
      return null;
    }
    const trimmed = value.trim();
    return trimmed.length > 0 ? trimmed : null;
  } catch {
    return null;
  }
};

const AuthTokenContext = createContext<AuthTokenContextValue | undefined>(undefined);

export const AuthTokenProvider = ({ children }: { children: ReactNode }) => {
  const [token, setTokenState] = useState<string | null>(() => {
    return readAccessToken();
  });
  const [sessionName, setSessionName] = useState<string | null>(() => readCookie('wishyMemberName'));
  const [profileLoaded, setProfileLoaded] = useState(false);

  useEffect(() => {
    const handleStorage = (event: StorageEvent) => {
      if (event.key === 'accessToken') {
        setTokenState(event.newValue);
      }
    };

    window.addEventListener('storage', handleStorage);
    return () => window.removeEventListener('storage', handleStorage);
  }, []);

  useEffect(() => {
    const handleAccessTokenUpdate = (event: Event) => {
      if (event instanceof CustomEvent) {
        const detail = typeof event.detail === 'string' ? event.detail : null;
        setTokenState(detail);
        return;
      }
      setTokenState(readAccessToken());
    };

    window.addEventListener('wishy:access-token-updated', handleAccessTokenUpdate as EventListener);
    return () =>
      window.removeEventListener('wishy:access-token-updated', handleAccessTokenUpdate as EventListener);
  }, []);

  useEffect(() => {
    setSessionName(readCookie('wishyMemberName'));
  }, []);

  const setToken = useCallback((value: string | null) => {
    setTokenState(value);
    try {
      if (value && value.trim().length > 0) {
        localStorage.setItem(ACCESS_TOKEN_KEY, value.trim());
      } else {
        localStorage.removeItem(ACCESS_TOKEN_KEY);
      }
    } catch {
      // ignore storage errors
    }
  }, []);

  useEffect(() => {
    setProfileLoaded(false);
  }, [token]);

  useEffect(() => {
    const cookieName = readCookie('wishyMemberName');
    const shouldFetch = Boolean(token) || (cookieName && !sessionName);
    if (!shouldFetch || profileLoaded) {
      return;
    }

    let cancelled = false;

    const hydrateProfile = async () => {
      try {
        const member = await fetchCurrentUser();
        if (!cancelled) {
          setSessionName(member.name ?? member.email ?? null);
          setProfileLoaded(true);
        }
      } catch (error) {
        const apiError = error as ApiError;
        if (!cancelled && apiError?.status === 401 && token) {
          setToken(null);
          try {
            localStorage.removeItem('refreshToken');
          } catch {
            // ignore storage errors
          }
          setProfileLoaded(true);
        } else if (!cancelled) {
          setProfileLoaded(true);
        }
      }
    };

    void hydrateProfile();

    return () => {
      cancelled = true;
    };
  }, [token, sessionName, setToken, profileLoaded]);

  const contextValue = useMemo<AuthTokenContextValue>(
    () => ({
      token,
      setToken,
      sessionName,
      setSessionName,
      isAuthenticated: Boolean(token) || Boolean(sessionName),
    }),
    [token, sessionName]
  );

  return (
    <AuthTokenContext.Provider value={contextValue}>{children}</AuthTokenContext.Provider>
  );
};

export const useAuthToken = () => {
  const context = useContext(AuthTokenContext);
  if (!context) {
    throw new Error('useAuthToken must be used within an AuthTokenProvider');
  }
  return context;
};
