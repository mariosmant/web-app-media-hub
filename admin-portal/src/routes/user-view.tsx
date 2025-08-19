import { PrivateRoute } from '../auth/PrivateRoute';
import UserView from '../pages/users/UserView';

export function Component() {
  return (
    <PrivateRoute>
      <UserView />
    </PrivateRoute>
  );
}
