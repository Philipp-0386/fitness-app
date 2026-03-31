export type SignUpPayload = {
  username: string;
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  dateOfBirth: string;
};

export type SuccessfulSignUpResponse = {
  username: string;
  email: string;
  firstName: string;
  lastName: string;
};
