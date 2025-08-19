import { createBrowserRouter } from 'react-router-dom';
import AppLayout from './layout/AppLayout';

const router = createBrowserRouter([
  {
    path: '/',
    element: <AppLayout />,
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
]);

export default router;
