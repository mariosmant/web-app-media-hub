import { useEffect, useMemo } from 'react';
import axios from 'axios';
import { useAuth } from 'react-oidc-context';

export const useApi = () => {
  const auth = useAuth();

  const instance = useMemo(() => {
    const client = axios.create({
      baseURL: import.meta.env.VITE_API_BASE_URL,
      headers: { 'Content-Type': 'application/json' },
    });

    const reqId = client.interceptors.request.use((config) => {
      const token = auth?.user?.access_token;
      if (token) {
        config.headers = config.headers ?? {};
        config.headers.Authorization = `Bearer ${token}`;
      }
      return config;
    });

    const resId = client.interceptors.response.use(
      (res) => res,
      async (error) => {
        // Optionally handle 401/403 here (e.g., trigger refresh or redirect to login)
        return Promise.reject(error);
      }
    );

    // Eject interceptors when auth changes or component unmounts
    return Object.assign(client, { __reqId: reqId, __resId: resId });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [auth?.user?.access_token]);

  useEffect(() => {
    return () => {
      if ((instance as any).__reqId !== undefined) {
        instance.interceptors.request.eject((instance as any).__reqId);
      }
      if ((instance as any).__resId !== undefined) {
        instance.interceptors.response.eject((instance as any).__resId);
      }
    };
  }, [instance]);

  return instance;
};
