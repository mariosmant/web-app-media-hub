import { createBrowserRouter } from 'react-router-dom';
import AppLayout from './layout/AppLayout';
import AppFallback from './components/AppFallback';
import { OIDCAuthProvider } from './auth/AuthProvider';

const router = createBrowserRouter(
  [
    {
      path: '/',
      element: (
        <OIDCAuthProvider>
          <AppLayout />
        </OIDCAuthProvider>
      ),
      hydrateFallbackElement: <AppFallback />,
      children: [
        { index: true, lazy: () => import('./routes/home') },
        { path: 'users', lazy: () => import('./routes/users') },
        { path: 'users/:id', lazy: () => import('./routes/user-view') },
        { path: 'users/:id/edit', lazy: () => import('./routes/user-edit') },
        { path: 'callback', lazy: () => import('./routes/callback') },
        { path: 'silent-renew', lazy: () => import('./routes/silent-renew') },
        { path: '*', lazy: () => import('./routes/not-found') },
      ],
    },
  ],
  {
    basename: import.meta.env.BASE_URL,
  }
);

export default router;
