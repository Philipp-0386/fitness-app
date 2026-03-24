import { ApiError } from "@/shared/errors"

import { fetchUserData } from "../api/server";
import type { UserData } from "./models";
import { mapUserData } from "./mapper";

import { fetchRoles } from "../api/server";
import type { Role } from "./models"
import { mapRole } from "./mapper"

export async function getUserDataAll(): Promise<UserData[]> {
    let data;
    try {
        data = await fetchUserData();
        return data.map(dto => mapUserData(dto));
    } catch(error) {
        if(error instanceof ApiError) {
            if(error.status === 404) {
                return[];
            }
        }
        throw error;
    }
}

export async function getRolesAll(): Promise<Role[]> {
    let data;
    try {
        data = await fetchRoles();
        return data.map(dto => mapRole(dto));
    } catch(error) {
        if(error instanceof ApiError) {
            if(error.status === 404) {
                return[];
            }
        }
        throw error;
    }
}