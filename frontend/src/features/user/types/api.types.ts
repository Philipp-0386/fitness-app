/** Mirrors the backend UserResponse record. */
export type User = {
  username: string;
  email: string;
  createdAt: string;
};

export type DeleteUserPayload = {
  password: string;
};

export type UpdateUserPayload = {
  username: string;
  email: string;
  currentPassword: string;
};

export type UpdatePasswordPayload = {
  currentPassword: string;
  newPassword: string;
};
