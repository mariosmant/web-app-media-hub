import { PrivateRoute } from '../auth/PrivateRoute';
import UserEdit from '../pages/users/UserEdit';

export function Component() {
  return (
    <PrivateRoute>
      <UserEdit />
    </PrivateRoute>
  );
}
