import { AuthProvider } from 'react-oidc-context';
import type { AuthProviderProps } from 'react-oidc-context';

const oidcConfig: AuthProviderProps = {
  authority: import.meta.env.VITE_OIDC_AUTHORITY,
  client_id: import.meta.env.VITE_OIDC_CLIENT_ID,
  redirect_uri: window.location.origin + '/callback',
  response_type: 'code',
  scope: 'openid profile email',
  automaticSilentRenew: true,
  silent_redirect_uri: window.location.origin + '/silent-renew',
  loadUserInfo: true,
  onSigninCallback: (user?: any) => { // TODO User type.
    // Remove code/state from URL
    window.history.replaceState({}, document.title, window.location.pathname);

    // Redirect back to the originating route if provided
    const returnTo =
      (user?.state as any)?.returnTo ||
      sessionStorage.getItem('returnTo') ||
      '/';

    sessionStorage.removeItem('returnTo');
    // Use replace to avoid back-button going to /callback
    window.location.replace(returnTo);
  },
};

export const OIDCAuthProvider = ({ children }: { children: React.ReactNode }) => (
  <AuthProvider {...oidcConfig}>{children}</AuthProvider>
);
