import { StrictMode, Suspense } from 'react';
import ReactDOM from 'react-dom/client';
import { RouterProvider } from 'react-router-dom';
import router from './router';
import { I18nProvider } from './i18n/i18n';

// Bootstrap CSS/JS and Icons
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.bundle.min.js';
import 'bootstrap-icons/font/bootstrap-icons.css';

import './index.css';
import './styles/App.css';
import { AppFallback } from './components/AppFallback';

ReactDOM.createRoot(document.getElementById('root') as HTMLElement).render(
  <StrictMode>
    <I18nProvider>
      <Suspense fallback={<AppFallback />}>
        <RouterProvider router={router} />
      </Suspense>
    </I18nProvider>
  </StrictMode>
);
