import { getRolesAll } from "../service/userDataServiceServer";
import type { Role } from "../service/models";
import { ApiError, NetworkError } from "@/shared/errors";

export default async function RoleShowUi() {
    let roles: Role[];
    try {
        roles = await getRolesAll();
    } catch (error) {
        if(error instanceof NetworkError) {
            return <p>network error (role)</p>
        }
        if(error instanceof ApiError) {
            return <p>api error (role)</p>
        }
        throw error;
    }

    if(roles.length === 0) {
        return <p>Keine Rollen gefunden!</p>
    }

    return (
        <ul>
            {roles.map(role => (
                <div key={role.roleId}>
                    <span>id: {role.roleId}</span>
                    <span>name: {role.roleName}</span>
                </div>
            ))}
        </ul>
    );
}