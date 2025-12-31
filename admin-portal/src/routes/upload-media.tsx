import { PrivateRoute } from '../auth/PrivateRoute';
import UploadMedia from '../pages/upload/UploadMedia';

export function Component() {
  return (
    <PrivateRoute>
      <UploadMedia />
    </PrivateRoute>
  );
}

export function ErrorBoundary() {
  return (
    <div className="container py-5">
      <div className="alert alert-danger mb-0">
        Couldn’t load the Upload page. Please try again.
      </div>
    </div>
  );
}
