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

/** Mirrors the backend UserSettingsResponse record. */
export type UserSettings = {
  weightUnit: 'KG' | 'LB';
  distanceUnit: 'KM' | 'MI';
  timeZone: string;
};

export type UpdateUserSettingsPayload = {
  weightUnit: 'KG' | 'LB';
  distanceUnit: 'KM' | 'MI';
  timeZone: string;
};
