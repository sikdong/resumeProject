const API_BASE_URL = process.env.REACT_APP_API_BASE_URL ?? 'http://localhost:8080';

type RequestOptions = RequestInit & { parseJson?: boolean };

type ApiError = Error & { status?: number };

const buildUrl = (path: string) => {
  if (path.startsWith('http')) {
    return path;
  }
  return `${API_BASE_URL}${path}`;
};

const readBody = async (response: Response) => {
  const contentType = response.headers.get('content-type');
  if (contentType && contentType.includes('application/json')) {
    return response.json();
  }
  return response.text();
};

export const apiFetch = async <T>(path: string, options: RequestOptions = {}): Promise<T> => {
  const headers = new Headers(options.headers);
  const token = localStorage.getItem('accessToken');

  if (token) {
    headers.set('Authorization', `Bearer ${token}`);
  }

  const shouldSetJson =
    !(options.body instanceof FormData) &&
    options.method &&
    options.method !== 'GET' &&
    !headers.has('Content-Type');

  if (shouldSetJson) {
    headers.set('Content-Type', 'application/json');
  }

  const response = await fetch(buildUrl(path), {
    ...options,
    headers,
    credentials: options.credentials ?? 'include',
  });

  if (!response.ok) {
    const payload = await readBody(response);
    const message = typeof payload === 'string' && payload.length > 0 ? payload : response.statusText;
    const error: ApiError = new Error(message);
    error.status = response.status;
    throw error;
  }

  if (response.status === 204) {
    return undefined as T;
  }

  const data = await readBody(response);
  return data as T;
};

export const api = {
  get: <T>(path: string, options?: RequestOptions) => apiFetch<T>(path, { ...options, method: 'GET' }),
  post: <T>(path: string, body?: BodyInit | null, options?: RequestOptions) =>
    apiFetch<T>(path, { ...options, method: 'POST', body }),
  put: <T>(path: string, body?: BodyInit | null, options?: RequestOptions) =>
    apiFetch<T>(path, { ...options, method: 'PUT', body }),
  del: <T>(path: string, options?: RequestOptions) => apiFetch<T>(path, { ...options, method: 'DELETE' }),
};

export type { ApiError };
