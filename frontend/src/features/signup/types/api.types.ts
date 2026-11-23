export type SignUpPayload = {
  username: string;
  email: string;
  password: string;
  language: 'EN' | 'DE';
};

export type SuccessfulSignUpResponse = {
  username: string;
  email: string;
};
