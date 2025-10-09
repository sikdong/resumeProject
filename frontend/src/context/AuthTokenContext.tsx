import { createContext, useContext, useEffect, useMemo, useState, ReactNode } from 'react';

type AuthTokenContextValue = {
  token: string | null;
  setToken: (value: string | null) => void;
  isAuthenticated: boolean;
};

const AuthTokenContext = createContext<AuthTokenContextValue | undefined>(undefined);

export const AuthTokenProvider = ({ children }: { children: ReactNode }) => {
  const [token, setTokenState] = useState<string | null>(() => {
    try {
      return localStorage.getItem('accessToken');
    } catch {
      return null;
    }
  });

  useEffect(() => {
    const handleStorage = (event: StorageEvent) => {
      if (event.key === 'accessToken') {
        setTokenState(event.newValue);
      }
    };

    window.addEventListener('storage', handleStorage);
    return () => window.removeEventListener('storage', handleStorage);
  }, []);

  const setToken = (value: string | null) => {
    setTokenState(value);
    try {
      if (value && value.trim().length > 0) {
        localStorage.setItem('accessToken', value.trim());
      } else {
        localStorage.removeItem('accessToken');
      }
    } catch {
      // ignore storage errors
    }
  };

  const contextValue = useMemo<AuthTokenContextValue>(
    () => ({ token, setToken, isAuthenticated: Boolean(token) }),
    [token]
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
