import { useEffect } from 'react';
import type { JSX } from 'react';
import { useAuth } from 'react-oidc-context';
import { useLocation } from 'react-router-dom';

export const PrivateRoute = ({ children }: { children: JSX.Element }) => {
  const auth = useAuth();
  const location = useLocation();

  useEffect(() => {
    if (!auth.isLoading && !auth.isAuthenticated && !auth.activeNavigator) {
      const returnTo = location.pathname + location.search;
      sessionStorage.setItem('returnTo', returnTo);
      void auth.signinRedirect({ state: { returnTo } });
    }
  }, [auth, location]);

  if (auth.isLoading || auth.activeNavigator) {
    return (
      <div className="container py-5" role="status" aria-live="polite">
        <div className="alert alert-info mb-0">Loading…</div>
      </div>
    );
  }

  if (!auth.isAuthenticated) {
    return (
      <div className="container py-5" role="status" aria-live="polite">
        <div className="alert alert-warning mb-0">Redirecting to sign in…</div>
      </div>
    );
  }

  return children;
};
