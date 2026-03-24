import { z } from 'zod';

export const RoleSchema = z.object({
  roleId: z.number(),
  roleName: z.string(),
});

export const UserDataSchema = z.object({
  id: z.number(),
  username: z.string(),
  email: z.string(),
  firstName: z.string(),
  lastName: z.string(),
  dateOfBirth: z.string(),
  createdAt: z.string(),
  role: RoleSchema,
});

export const UserDataDtoList = z.array(UserDataSchema);
export const RoleDtoList = z.array(RoleSchema);

export type UserDataDto = z.infer<typeof UserDataSchema>;
export type RoleDto = z.infer<typeof RoleSchema>;
