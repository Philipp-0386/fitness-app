import backendFetch from '@/shared/api/backend';
import { ApiError } from '@/shared/api/errors/api-error';
import { UserDataDtoList } from '../service/schemas';
import { RoleDtoList } from '../service/schemas';

export async function fetchUserData() {
  const res = await backendFetch('/backend/smoketest/users');
  if (!res.ok) {
    throw new ApiError({
      status: res.status,
      code: 'USERDATA_FETCH_FAILED',
      message: 'Failed to fetch user data',
    });
  }
  const data: unknown = await res.json();
  return UserDataDtoList.parse(data);
}

export async function fetchRoles() {
  const res = await backendFetch('/backend/smoketest/roles');
  if (!res.ok) {
    throw new ApiError({
      status: res.status,
      code: 'ROLES_FETCH_FAILED',
      message: 'Failed to fetch roles',
    });
  }
  const data: unknown = await res.json();
  return RoleDtoList.parse(data);
}
