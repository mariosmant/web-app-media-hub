import { PrivateRoute } from '../auth/PrivateRoute';
import UsersList from '../pages/users/UsersList';

export function Component() {
  return (
    <PrivateRoute>
      <UsersList />
    </PrivateRoute>
  );
}

export function ErrorBoundary() {
  return (
    <div className="container py-5">
      <div className="alert alert-danger mb-0">
        Couldn’t load the Users page. Please try again.
      </div>
    </div>
  );
}
