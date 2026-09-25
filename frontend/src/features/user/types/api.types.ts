/** Mirrors the backend UserResponse record. */
export type User = {
  username: string;
  email: string;
  createdAt: string;
};

export type DeleteUserPayload = {
  password: string;
};
