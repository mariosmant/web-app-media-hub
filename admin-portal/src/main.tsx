import { StrictMode, Suspense } from 'react';
import ReactDOM from 'react-dom/client';
import { RouterProvider } from 'react-router-dom';
import router from './router';
import { OIDCAuthProvider } from './auth/AuthProvider';
import { I18nProvider } from './i18n/i18n';

// Bootstrap CSS/JS and Icons
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.bundle.min.js';
import 'bootstrap-icons/font/bootstrap-icons.css';

import './index.css';
import './styles/App.css';

ReactDOM.createRoot(document.getElementById('root') as HTMLElement).render(
  <StrictMode>
    <I18nProvider>
      <OIDCAuthProvider>
        <Suspense fallback={<div className="app-fallback" role="status" aria-live="polite">Loading…</div>}>
          <RouterProvider router={router} />
        </Suspense>
      </OIDCAuthProvider>
    </I18nProvider>
  </StrictMode>
);
