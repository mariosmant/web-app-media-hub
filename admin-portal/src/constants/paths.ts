// Base URL for routing and assets
export const APP_BASE_URL = import.meta.env.VITE_BASE_PATH;
export const NORMALIZED_APP_BASE_URL = APP_BASE_URL.endsWith('/')
    ? APP_BASE_URL.slice(0, -1)
    : APP_BASE_URL;

// API Base URL
export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

// OpenID constants
export const OIDC_AUTHORITY = import.meta.env.VITE_OIDC_AUTHORITY;
export const OIDC_CLIENT_ID = import.meta.env.VITE_OIDC_CLIENT_ID;
export const OIDC_REDIRECT_URI_BASE = window.location.origin + NORMALIZED_APP_BASE_URL;
