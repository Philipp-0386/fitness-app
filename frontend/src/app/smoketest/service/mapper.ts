import type { Role } from "./models"
import type { RoleDto } from "./schemas"
import type { UserData } from "./models";
import type { UserDataDto } from "./schemas";

export function mapRole(dto: RoleDto): Role {
    return {
        roleId: dto.roleId,
        roleName: dto.roleName,
    };
}

export function mapUserData(dto: UserDataDto): UserData {
    return {
        id: dto.id,
        username: dto.username,
        email: dto.email,
        firstName: dto.firstName,
        lastName: dto.lastName,
        dateOfBirth: dto.dateOfBirth,
        createdAt: dto.createdAt,
        role: mapRole(dto.role),
    };
}