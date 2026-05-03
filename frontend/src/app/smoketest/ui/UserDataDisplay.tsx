import { getUserDataAll } from '../service/userDataServiceServer';
import type { UserData } from '../service/models';
import { ApiError } from '@/shared/api/errors/api-error';
import { NetworkError } from '@/shared/api/errors/network-error';

export default async function UserDataShowUi() {
  let users: UserData[];
  try {
    users = await getUserDataAll();
  } catch (error) {
    if (error instanceof ApiError) {
      return <p>api error (user)</p>;
    }
    if (error instanceof NetworkError) {
      return <p>network error (user)</p>;
    }
    throw error;
  }

  if (users.length === 0) {
    return <p>Keine Benutzer gefunden!</p>;
  }

  return (
    <ul>
      {users.map((user) => (
        <li key={user.id}>
          <span>id: {user.id}</span>
          <span>username: {user.username}</span>
          <span>email: {user.email}</span>
          <span>firstName: {user.firstName}</span>
          <span>lastName: {user.lastName}</span>
          <span>dateOfBirth: {user.dateOfBirth}</span>
          <span>createdAt: {user.createdAt}</span>
          <span>role (id): {user.role?.roleId ?? '-'}</span>
          <span>role (name): {user.role?.roleName ?? '-'}</span>
        </li>
      ))}
    </ul>
  );
}
