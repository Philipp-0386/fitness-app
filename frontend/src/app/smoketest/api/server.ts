import backendFetch from "@/shared/api/backend";
import ApiError from "./apiError";
import { UserDataDtoList } from  "../service/schemas";
import { RoleDtoList } from "../service/schemas";

export async function fetchUserData() {
    const res = await backendFetch("/backend/smoketest/users");
    if(!res.ok) {
        throw new ApiError(res.status, "userdata show request failed");
    }
    const data: unknown = await res.json();
    return UserDataDtoList.parse(data);
}

export async function fetchRoles() {
    const res = await backendFetch("/backend/smoketest/roles");
    if(!res.ok) {
        throw new ApiError(res.status, "roles show request failed");
    }
    const data: unknown = await res.json();
    return RoleDtoList.parse(data);
}