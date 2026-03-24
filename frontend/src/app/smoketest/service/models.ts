export type Role = {
    roleId: number;
    roleName: string;
}

export type UserData = {
    id: number;
    username: string;
    email: string;
    firstName: string;
    lastName: string;
    dateOfBirth: string;
    createdAt: string;
    role: Role
};